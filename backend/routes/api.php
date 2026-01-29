<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

// Public authentication routes
Route::post('/register', [App\Http\Controllers\Api\AuthApiController::class, 'register']);
Route::post('/login', [App\Http\Controllers\Api\AuthApiController::class, 'login']);

// Protected routes (require authentication)
Route::middleware('auth:sanctum')->group(function () {
    Route::post('/logout', [App\Http\Controllers\Api\AuthApiController::class, 'logout']);
    Route::get('/profile', [App\Http\Controllers\Api\AuthApiController::class, 'profile']);
    Route::put('/profile', [App\Http\Controllers\Api\AuthApiController::class, 'updateProfile']);
    Route::post('/profile/avatar', [App\Http\Controllers\Api\AuthApiController::class, 'uploadAvatar']);
    Route::delete('/profile/avatar', [App\Http\Controllers\Api\AuthApiController::class, 'deleteAvatar']);
    Route::post('/profile/password', [App\Http\Controllers\Api\AuthApiController::class, 'changePassword']);
    Route::delete('/profile', [App\Http\Controllers\Api\AuthApiController::class, 'deleteAccount']);
});

// Public API routes
Route::get('/shelters', [App\Http\Controllers\Api\ShelterApiController::class, 'index']);
Route::get('/shelters/{id}', [App\Http\Controllers\Api\ShelterApiController::class, 'show']);
Route::post('/shelters', [App\Http\Controllers\Api\ShelterApiController::class, 'store']);
Route::put('/shelters/{id}', [App\Http\Controllers\Api\ShelterApiController::class, 'update']);
Route::delete('/shelters/{id}', [App\Http\Controllers\Api\ShelterApiController::class, 'destroy']);
Route::put('/shelters/{id}/occupancy', [App\Http\Controllers\Api\ShelterApiController::class, 'updateOccupancy']);

// Report routes (support both authenticated and anonymous users with device ID)
Route::post('/reports', [App\Http\Controllers\Api\ReportApiController::class, 'store']);
Route::get('/reports', [App\Http\Controllers\Api\ReportApiController::class, 'index']);
Route::put('/reports/{id}/verify', [App\Http\Controllers\Api\ReportApiController::class, 'verify']);
Route::get('/reports/{id}/vote', [App\Http\Controllers\Api\ReportApiController::class, 'checkVote']);
Route::get('/votes', [App\Http\Controllers\Api\ReportApiController::class, 'getUserVotes']);
