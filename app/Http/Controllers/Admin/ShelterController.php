<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Shelter;
use Illuminate\Http\Request;

class ShelterController extends Controller
{
    public function index()
    {
        $shelters = Shelter::all();
        return view('admin.shelters.index', compact('shelters'));
    }

    public function create()
    {
        return view('admin.shelters.create');
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'description' => 'required|string',
            'latitude' => 'required|numeric',
            'longitude' => 'required|numeric',
        ]);

        Shelter::create($validated);

        return redirect()->route('admin.shelters.index')->with('success', 'Shelter created successfully.');
    }

    public function edit(Shelter $shelter)
    {
        return view('admin.shelters.edit', compact('shelter'));
    }

    public function update(Request $request, Shelter $shelter)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'description' => 'required|string',
            'latitude' => 'required|numeric',
            'longitude' => 'required|numeric',
        ]);

        $shelter->update($validated);

        return redirect()->route('admin.shelters.index')->with('success', 'Shelter updated successfully.');
    }

    public function destroy(Shelter $shelter)
    {
        $shelter->delete();
        return redirect()->route('admin.shelters.index')->with('success', 'Shelter deleted successfully.');
    }
}
