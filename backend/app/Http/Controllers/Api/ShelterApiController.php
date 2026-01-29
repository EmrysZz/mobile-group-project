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
    public function index(Request $request)
    {
        $query = Shelter::query();

        // Filter by status if provided
        if ($request->has('status')) {
            $query->where('status', $request->status);
        }

        // Only show open shelters by default for non-admin requests
        if (!$request->has('all')) {
            $query->where('status', '!=', 'closed');
        }

        $shelters = $query->orderBy('created_at', 'desc')->get()->map(function ($shelter) {
            return $this->formatShelter($shelter);
        });

        return response()->json($shelters, 200);
    }

    /**
     * Store a new shelter
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'description' => 'required|string|max:1000',
            'address' => 'nullable|string|max:255',
            'phone' => 'nullable|string|max:20',
            'latitude' => 'required|numeric|between:-90,90',
            'longitude' => 'required|numeric|between:-180,180',
            'capacity' => 'nullable|integer|min:1',
        ]);

        // Get user ID if authenticated
        $userId = null;
        if ($request->user()) {
            $userId = $request->user()->id;
        }

        // Get device ID from header
        $deviceId = $request->header('X-Device-Id');

        $shelter = Shelter::create([
            'name' => $validated['name'],
            'description' => $validated['description'],
            'address' => $validated['address'] ?? null,
            'phone' => $validated['phone'] ?? null,
            'latitude' => $validated['latitude'],
            'longitude' => $validated['longitude'],
            'capacity' => $validated['capacity'] ?? null,
            'current_occupancy' => 0,
            'status' => 'open',
            'submitted_by' => $userId,
            'device_id' => $deviceId,
            'is_verified' => false,
        ]);

        return response()->json([
            'message' => 'Shelter added successfully',
            'shelter' => $this->formatShelter($shelter)
        ], 201);
    }

    /**
     * Get a single shelter
     */
    public function show($id)
    {
        $shelter = Shelter::find($id);

        if (!$shelter) {
            return response()->json([
                'message' => 'Shelter not found'
            ], 404);
        }

        return response()->json($this->formatShelter($shelter), 200);
    }

    /**
     * Update a shelter
     */
    public function update(Request $request, $id)
    {
        $shelter = Shelter::find($id);

        if (!$shelter) {
            return response()->json([
                'message' => 'Shelter not found'
            ], 404);
        }

        // Check if user has permission to update
        $userId = $request->user() ? $request->user()->id : null;
        $deviceId = $request->header('X-Device-Id');

        $canUpdate = false;
        if ($userId && $shelter->submitted_by === $userId) {
            $canUpdate = true;
        } elseif ($deviceId && $shelter->device_id === $deviceId) {
            $canUpdate = true;
        }

        if (!$canUpdate) {
            return response()->json([
                'message' => 'You do not have permission to update this shelter'
            ], 403);
        }

        $validated = $request->validate([
            'name' => 'sometimes|string|max:255',
            'description' => 'sometimes|string|max:1000',
            'address' => 'nullable|string|max:255',
            'phone' => 'nullable|string|max:20',
            'latitude' => 'sometimes|numeric|between:-90,90',
            'longitude' => 'sometimes|numeric|between:-180,180',
            'capacity' => 'nullable|integer|min:1',
            'current_occupancy' => 'sometimes|integer|min:0',
            'status' => 'sometimes|in:open,full,closed',
        ]);

        $shelter->update($validated);

        return response()->json([
            'message' => 'Shelter updated successfully',
            'shelter' => $this->formatShelter($shelter)
        ], 200);
    }

    /**
     * Delete a shelter
     */
    public function destroy(Request $request, $id)
    {
        $shelter = Shelter::find($id);

        if (!$shelter) {
            return response()->json([
                'message' => 'Shelter not found'
            ], 404);
        }

        // Check if user has permission to delete
        $userId = $request->user() ? $request->user()->id : null;
        $deviceId = $request->header('X-Device-Id');

        $canDelete = false;
        if ($userId && $shelter->submitted_by === $userId) {
            $canDelete = true;
        } elseif ($deviceId && $shelter->device_id === $deviceId) {
            $canDelete = true;
        }

        if (!$canDelete) {
            return response()->json([
                'message' => 'You do not have permission to delete this shelter'
            ], 403);
        }

        $shelter->delete();

        return response()->json([
            'message' => 'Shelter deleted successfully'
        ], 200);
    }

    /**
     * Update shelter occupancy
     */
    public function updateOccupancy(Request $request, $id)
    {
        $shelter = Shelter::find($id);

        if (!$shelter) {
            return response()->json([
                'message' => 'Shelter not found'
            ], 404);
        }

        $validated = $request->validate([
            'current_occupancy' => 'required|integer|min:0',
        ]);

        $shelter->current_occupancy = $validated['current_occupancy'];

        // Auto-update status based on capacity
        if ($shelter->capacity !== null) {
            if ($shelter->current_occupancy >= $shelter->capacity) {
                $shelter->status = 'full';
            } elseif ($shelter->status === 'full') {
                $shelter->status = 'open';
            }
        }

        $shelter->save();

        return response()->json([
            'message' => 'Occupancy updated successfully',
            'shelter' => $this->formatShelter($shelter)
        ], 200);
    }

    /**
     * Format shelter for API response
     */
    private function formatShelter(Shelter $shelter): array
    {
        return [
            'id' => $shelter->id,
            'name' => $shelter->name,
            'description' => $shelter->description,
            'address' => $shelter->address,
            'phone' => $shelter->phone,
            'latitude' => (float) $shelter->latitude,
            'longitude' => (float) $shelter->longitude,
            'capacity' => $shelter->capacity,
            'current_occupancy' => $shelter->current_occupancy,
            'available_space' => $shelter->available_space,
            'status' => $shelter->status,
            'is_verified' => $shelter->is_verified,
            'created_at' => $shelter->created_at->toDateTimeString(),
            'updated_at' => $shelter->updated_at->toDateTimeString(),
            // Legacy fields for backward compatibility
            'incident_type' => 'Shelter',
            'report_time' => $shelter->created_at->toDateTimeString(),
            'user_name' => $shelter->name,
            'verification_count' => $shelter->is_verified ? 100 : 0,
        ];
    }
}
