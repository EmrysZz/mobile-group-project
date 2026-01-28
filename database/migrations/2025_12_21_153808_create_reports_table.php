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
        Schema::create('reports', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->nullable()->constrained()->onDelete('cascade'); // Enable nullable if anonymous
            $table->string('user_name'); // Requested in spec
            $table->string('incident_type', 50); // Spec: 50 chars
            $table->text('description');
            $table->string('user_agent')->nullable();
            $table->integer('verification_count')->default(0); // Spec: Default 0
            $table->string('status')->default('Active'); // Spec: Default 'Active'
            $table->timestamp('report_time')->useCurrent(); // Spec: report_time 
            $table->decimal('latitude', 10, 8);
            $table->decimal('longitude', 11, 8);
            $table->timestamps(); // Handles time/date automatically
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('reports');
    }
};
