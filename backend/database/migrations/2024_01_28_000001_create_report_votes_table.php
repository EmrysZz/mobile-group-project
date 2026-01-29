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
        Schema::create('report_votes', function (Blueprint $table) {
            $table->id();
            $table->foreignId('report_id')->constrained()->onDelete('cascade');
            $table->foreignId('user_id')->nullable()->constrained()->onDelete('cascade');
            $table->string('device_id')->nullable(); // For anonymous users
            $table->enum('vote_type', ['upvote', 'downvote']);
            $table->timestamps();

            // Ensure one vote per user per report
            $table->unique(['report_id', 'user_id']);
            // Ensure one vote per device per report (for anonymous users)
            $table->index(['report_id', 'device_id']);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('report_votes');
    }
};
