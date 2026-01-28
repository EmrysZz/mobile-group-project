<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Shelter;
use Illuminate\Http\Request;

class ShelterApiController extends Controller
{
    /**
     * Return JSON list of all shelters.
     */
    public function index()
    {
        $shelters = Shelter::all()->map(function ($shelter) {
            return [
                'id' => $shelter->id,
                'incident_type' => 'Shelter', // Critical for app icon
                'latitude' => (float) $shelter->latitude,
                'longitude' => (float) $shelter->longitude,
                'report_time' => $shelter->created_at->toDateTimeString(),
                'user_name' => $shelter->name, // Use shelter name as reporter
                'description' => $shelter->description,
                'verification_count' => 100, // Trusted source
            ];
        });

        return response()->json($shelters, 200);
    }
}
