<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->string('phone', 20)->nullable()->after('email');
            $table->text('bio')->nullable()->after('phone');
            $table->string('avatar')->nullable()->after('bio');
            $table->string('address')->nullable()->after('avatar');
            $table->string('emergency_contact', 20)->nullable()->after('address');
            $table->string('emergency_contact_name')->nullable()->after('emergency_contact');
            $table->timestamp('email_verified_at')->nullable()->after('emergency_contact_name');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->dropColumn([
                'phone',
                'bio',
                'avatar',
                'address',
                'emergency_contact',
                'emergency_contact_name',
                'email_verified_at'
            ]);
        });
    }
};
