<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

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
}
