<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Shelter extends Model
{
    protected $fillable = [
        'name',
        'description',
        'address',
        'phone',
        'latitude',
        'longitude',
        'capacity',
        'current_occupancy',
        'status',
        'submitted_by',
        'device_id',
        'is_verified',
    ];

    protected $casts = [
        'latitude' => 'float',
        'longitude' => 'float',
        'capacity' => 'integer',
        'current_occupancy' => 'integer',
        'is_verified' => 'boolean',
    ];

    /**
     * Get the user who submitted this shelter
     */
    public function submitter(): BelongsTo
    {
        return $this->belongsTo(User::class, 'submitted_by');
    }

    /**
     * Check if shelter has available space
     */
    public function hasSpace(): bool
    {
        if ($this->capacity === null) {
            return $this->status === 'open';
        }
        return $this->current_occupancy < $this->capacity && $this->status === 'open';
    }

    /**
     * Get available space count
     */
    public function getAvailableSpaceAttribute(): ?int
    {
        if ($this->capacity === null) {
            return null;
        }
        return max(0, $this->capacity - $this->current_occupancy);
    }
}
