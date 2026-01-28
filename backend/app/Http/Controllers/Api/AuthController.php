<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Google\Client as GoogleClient;

class AuthController extends Controller
{
    public function googleLogin(Request $request)
    {
        $request->validate(['id_token' => 'required']);

        $client = new GoogleClient(['client_id' => 'YOUR_GOOGLE_CLIENT_ID_FROM_ANDROID']);

        try {
            $payload = $client->verifyIdToken($request->id_token);

            if ($payload) {
                $googleId = $payload['sub'];
                $email = $payload['email'];
                $name = $payload['name'];

                // Find or create user
                $user = User::updateOrCreate(
                    ['email' => $email],
                    ['name' => $name, 'google_id' => $googleId]
                );

                // Create token for Android
                $token = $user->createToken('android_auth')->plainTextToken;

                return response()->json(['token' => $token, 'user' => $user]);
            }
        } catch (\Exception $e) {
            return response()->json(['error' => 'Invalid Token'], 401);
        }

        return response()->json(['error' => 'Authentication Failed'], 401);
    }
}
