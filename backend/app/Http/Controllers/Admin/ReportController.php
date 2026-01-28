<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Report;
use Illuminate\Http\Request;

class ReportController extends Controller
{
    public function index()
    {
        $reports = Report::orderBy('report_time', 'desc')->paginate(20);
        return view('admin.reports.index', compact('reports'));
    }

    public function show(Report $report)
    {
        return view('admin.reports.show', compact('report'));
    }

    public function updateStatus(Request $request, Report $report)
    {
        $validated = $request->validate([
            'status' => 'required|string|in:Active,Verified,False,Resolved',
        ]);

        $report->update(['status' => $validated['status']]);

        return redirect()->back()->with('success', 'Report status updated.');
    }
}
