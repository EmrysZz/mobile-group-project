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
});

// Public API routes (no auth required for now)
Route::get('/shelters', [App\Http\Controllers\Api\ShelterApiController::class, 'index']);
Route::post('/reports', [App\Http\Controllers\Api\ReportApiController::class, 'store']);
Route::get('/reports', [App\Http\Controllers\Api\ReportApiController::class, 'index']);
Route::put('/reports/{id}/verify', [App\Http\Controllers\Api\ReportApiController::class, 'verify']);
Route::get('/news', [App\Http\Controllers\Api\NewsApiController::class, 'index']);
