<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Report extends Model
{
    protected $fillable = [
        'user_id',
        'user_name',
        'incident_type',
        'description',
        'latitude',
        'longitude',
        'user_agent',
        'verification_count',
        'status',
        'report_time',
    ];

    /**
     * Get the votes for the report.
     */
    public function votes(): HasMany
    {
        return $this->hasMany(ReportVote::class);
    }

    /**
     * Get the user who created the report.
     */
    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    /**
     * Check if a user has voted on this report.
     */
    public function hasUserVoted(?int $userId, ?string $deviceId = null): bool
    {
        if ($userId) {
            return $this->votes()->where('user_id', $userId)->exists();
        }

        if ($deviceId) {
            return $this->votes()->where('device_id', $deviceId)->exists();
        }

        return false;
    }

    /**
     * Get user's vote type on this report.
     */
    public function getUserVoteType(?int $userId, ?string $deviceId = null): ?string
    {
        $vote = null;

        if ($userId) {
            $vote = $this->votes()->where('user_id', $userId)->first();
        } elseif ($deviceId) {
            $vote = $this->votes()->where('device_id', $deviceId)->first();
        }

        return $vote?->vote_type;
    }
}
