<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Report;
use App\Models\ReportVote;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class ReportApiController extends Controller
{
    /**
     * Accept JSON data for new reports.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'user_name' => 'required|string|max:255',
            'incident_type' => 'required|string|max:50',
            'description' => 'required|string',
            'latitude' => 'required|numeric',
            'longitude' => 'required|numeric',
            'user_agent' => 'nullable|string|max:255',
        ]);

        // Auto-capture user agent if not provided in body, but usually from header
        $userAgent = $request->user_agent ?? $request->header('User-Agent');

        $report = Report::create([
            'user_id' => $request->user()?->id, // If authenticated
            'user_name' => $validated['user_name'],
            'incident_type' => $validated['incident_type'],
            'description' => $validated['description'],
            'latitude' => $validated['latitude'],
            'longitude' => $validated['longitude'],
            'user_agent' => $userAgent,
            'report_time' => now(),
        ]);

        return response()->json($report, 201);
    }

    /**
     * List all reports with user's vote status.
     */
    public function index(Request $request)
    {
        $reports = Report::orderBy('report_time', 'desc')->get();

        // Get user ID or device ID for vote tracking
        $userId = Auth::id();
        $deviceId = $request->header('X-Device-Id');

        // Add vote status to each report
        $reportsWithVotes = $reports->map(function ($report) use ($userId, $deviceId) {
            $reportArray = $report->toArray();
            $reportArray['user_vote'] = $report->getUserVoteType($userId, $deviceId);
            $reportArray['has_voted'] = $report->hasUserVoted($userId, $deviceId);
            return $reportArray;
        });

        return response()->json($reportsWithVotes);
    }

    /**
     * Verify (upvote/downvote) a report.
     * Each user can only vote once per report.
     */
    public function verify(Request $request, $id)
    {
        $validated = $request->validate([
            'value' => 'required|integer|in:-1,1', // Only accept +1 or -1
        ]);

        $report = Report::findOrFail($id);
        $userId = Auth::id();
        $deviceId = $request->header('X-Device-Id');

        // Check if user has already voted
        $existingVote = null;

        if ($userId) {
            $existingVote = ReportVote::where('report_id', $id)
                ->where('user_id', $userId)
                ->first();
        } elseif ($deviceId) {
            $existingVote = ReportVote::where('report_id', $id)
                ->where('device_id', $deviceId)
                ->first();
        }

        if ($existingVote) {
            return response()->json([
                'message' => 'You have already voted on this report',
                'vote_type' => $existingVote->vote_type,
                'report' => $report
            ], 409); // Conflict status code
        }

        // Record the vote
        $voteType = $validated['value'] === 1 ? 'upvote' : 'downvote';

        ReportVote::create([
            'report_id' => $id,
            'user_id' => $userId,
            'device_id' => $deviceId,
            'vote_type' => $voteType,
        ]);

        // Update verification count
        $report->verification_count += $validated['value'];
        $report->save();

        return response()->json([
            'message' => 'Vote recorded successfully',
            'vote_type' => $voteType,
            'report' => $report
        ]);
    }

    /**
     * Get all votes by the current user.
     */
    public function getUserVotes(Request $request)
    {
        $userId = Auth::id();
        $deviceId = $request->header('X-Device-Id');

        $query = ReportVote::query();

        if ($userId) {
            $query->where('user_id', $userId);
        } elseif ($deviceId) {
            $query->where('device_id', $deviceId);
        } else {
            return response()->json([]);
        }

        $votes = $query->get()->mapWithKeys(function ($vote) {
            return [$vote->report_id => $vote->vote_type];
        });

        return response()->json($votes);
    }

    /**
     * Check if user has voted on a specific report.
     */
    public function checkVote(Request $request, $id)
    {
        $report = Report::findOrFail($id);
        $userId = Auth::id();
        $deviceId = $request->header('X-Device-Id');

        $hasVoted = $report->hasUserVoted($userId, $deviceId);
        $voteType = $report->getUserVoteType($userId, $deviceId);

        return response()->json([
            'has_voted' => $hasVoted,
            'vote_type' => $voteType,
        ]);
    }
}
