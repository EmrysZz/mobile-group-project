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
        Schema::table('shelters', function (Blueprint $table) {
            $table->string('address')->nullable()->after('description');
            $table->string('phone', 20)->nullable()->after('address');
            $table->integer('capacity')->nullable()->after('phone');
            $table->integer('current_occupancy')->default(0)->after('capacity');
            $table->enum('status', ['open', 'full', 'closed'])->default('open')->after('current_occupancy');
            $table->foreignId('submitted_by')->nullable()->constrained('users')->onDelete('set null')->after('status');
            $table->string('device_id')->nullable()->after('submitted_by');
            $table->boolean('is_verified')->default(false)->after('device_id');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('shelters', function (Blueprint $table) {
            $table->dropForeign(['submitted_by']);
            $table->dropColumn([
                'address',
                'phone',
                'capacity',
                'current_occupancy',
                'status',
                'submitted_by',
                'device_id',
                'is_verified'
            ]);
        });
    }
};
