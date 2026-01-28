<x-admin-layout>
    <x-slot name="header">
        <h2 class="font-semibold text-xl text-brand-orange leading-tight">
            {{ __('Report Details') }} #{{ $report->id }}
        </h2>
    </x-slot>

    <div class="py-12">
        <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
            <div class="bg-brand-card/80 backdrop-blur-sm overflow-hidden shadow-lg sm:rounded-lg border border-white/10">
                <div class="p-6 text-gray-100">
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <h3 class="text-lg font-medium text-brand-orange">Incident Information</h3>
                            <div class="mt-4">
                                <p class="text-sm text-gray-400">Type</p>
                                <p class="text-lg font-semibold">{{ $report->incident_type }}</p>
                            </div>
                            <div class="mt-4">
                                <p class="text-sm text-gray-400">Description</p>
                                <p class="text-base text-gray-200">{{ $report->description }}</p>
                            </div>
                            <div class="mt-4">
                                <p class="text-sm text-gray-400">Reported By</p>
                                <p class="text-base text-gray-200">{{ $report->user_name }} <span class="text-xs text-gray-500">({{ $report->user_agent }})</span></p>
                            </div>
                            <div class="mt-4">
                                <p class="text-sm text-gray-400">Time</p>
                                <p class="text-base text-gray-200">{{ $report->report_time }}</p>
                            </div>
                            <div class="mt-4">
                                <p class="text-sm text-gray-400">Verification Score</p>
                                <p class="text-base text-gray-200">{{ $report->verification_count }}</p>
                            </div>
                        </div>

                        <div>
                            <h3 class="text-lg font-medium text-brand-orange">Location & Status</h3>
                            <div class="mt-4">
                                <p class="text-sm text-gray-400">Coordinates</p>
                                <p class="text-base mb-1">{{ $report->latitude }}, {{ $report->longitude }}</p>
                                <a href="https://www.google.com/maps/search/?api=1&query={{ $report->latitude }},{{ $report->longitude }}" target="_blank" class="text-blue-400 hover:text-blue-300 text-sm underline">View on Google Maps</a>
                            </div>

                            <div class="mt-8 border-t border-gray-700 pt-4">
                                <form action="{{ route('admin.reports.updateStatus', $report) }}" method="POST">
                                    @csrf
                                    @method('PATCH')
                                    <x-input-label for="status" :value="__('Update Status')" class="text-gray-300" />
                                    <div class="flex mt-1">
                                        <select id="status" name="status" class="block w-full bg-gray-900 border-gray-700 text-gray-100 focus:ring-brand-orange focus:border-brand-orange rounded-md shadow-sm">
                                            <option value="Active" {{ $report->status == 'Active' ? 'selected' : '' }}>Active</option>
                                            <option value="Verified" {{ $report->status == 'Verified' ? 'selected' : '' }}>Verified (Confirmed)</option>
                                            <option value="False" {{ $report->status == 'False' ? 'selected' : '' }}>False Report</option>
                                            <option value="Resolved" {{ $report->status == 'Resolved' ? 'selected' : '' }}>Resolved</option>
                                        </select>
                                        <x-primary-button class="ml-4 bg-gradient-to-r from-brand-red to-brand-orange border-none hover:opacity-90">
                                            {{ __('Update') }}
                                        </x-primary-button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</x-admin-layout>
