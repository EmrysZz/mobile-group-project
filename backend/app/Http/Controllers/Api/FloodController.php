<?php

namespace App\Http\Controllers\Api;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Location;
use App\Models\Report;

class FloodController extends Controller
{
    // 1. Get all markers for the map
    public function getMapData() {
        // Return admin locations (shelters) AND user reports (floods)
        return response()->json([
            'shelters' => Location::all(),
            'incidents' => Report::with('user:id,name')->latest()->take(50)->get()
        ]);
    }

    // 2. Mobile app sends a report
    public function storeReport(Request $request) {
        $request->validate([
            'latitude' => 'required',
            'longitude' => 'required',
            'incident_type' => 'required',
            'description' => 'required',
        ]);

        $report = Report::create([
            'user_id' => $request->user()->id, // Automatically gets ID from token
            'latitude' => $request->latitude,
            'longitude' => $request->longitude,
            'incident_type' => $request->incident_type,
            'description' => $request->description,
            'user_agent' => $request->header('User-Agent'), // Capture device info
        ]);

        return response()->json(['message' => 'Report saved!', 'report' => $report]);
    }
}
