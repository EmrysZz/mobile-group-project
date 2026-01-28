<x-admin-layout>
    <x-slot name="header">
        <h2 class="font-semibold text-xl text-brand-orange leading-tight">
            {{ __('Dashboard') }}
        </h2>
    </x-slot>

    <div class="py-12">
        <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                <!-- Total Reports -->
                <div class="bg-brand-card/80 backdrop-blur-sm overflow-hidden shadow-lg sm:rounded-lg border border-white/10">
                    <div class="p-6 text-gray-100">
                        <div class="flex items-center">
                            <div class="p-3 rounded-full bg-brand-orange/20 text-brand-orange">
                                <svg class="h-8 w-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                                </svg>
                            </div>
                            <div class="ml-4">
                                <p class="text-sm font-medium text-gray-400">Total Reports</p>
                                <p class="text-2xl font-semibold">{{ $stats['reports'] }}</p>
                            </div>
                        </div>
                        <div class="mt-4">
                            <a href="{{ route('admin.reports.index') }}" class="text-sm text-brand-orange hover:text-white">View all reports &rarr;</a>
                        </div>
                    </div>
                </div>

                <!-- Verified Shelters -->
                <div class="bg-brand-card/80 backdrop-blur-sm overflow-hidden shadow-lg sm:rounded-lg border border-white/10">
                    <div class="p-6 text-gray-100">
                        <div class="flex items-center">
                            <div class="p-3 rounded-full bg-blue-500/20 text-blue-500">
                                <svg class="h-8 w-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
                                </svg>
                            </div>
                            <div class="ml-4">
                                <p class="text-sm font-medium text-gray-400">Active Shelters</p>
                                <p class="text-2xl font-semibold">{{ $stats['shelters'] }}</p>
                            </div>
                        </div>
                        <div class="mt-4">
                            <a href="{{ route('admin.shelters.index') }}" class="text-sm text-blue-400 hover:text-white">Manage shelters &rarr;</a>
                        </div>
                    </div>
                </div>

                <!-- News Updates -->
                <div class="bg-brand-card/80 backdrop-blur-sm overflow-hidden shadow-lg sm:rounded-lg border border-white/10">
                    <div class="p-6 text-gray-100">
                        <div class="flex items-center">
                            <div class="p-3 rounded-full bg-green-500/20 text-green-500">
                                <svg class="h-8 w-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z"/>
                                </svg>
                            </div>
                            <div class="ml-4">
                                <p class="text-sm font-medium text-gray-400">News Updates</p>
                                <p class="text-2xl font-semibold">{{ $stats['news'] }}</p>
                            </div>
                        </div>
                        <div class="mt-4">
                            <a href="{{ route('admin.news.index') }}" class="text-sm text-green-400 hover:text-white">Publish updates &rarr;</a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Recent Activity Table -->
            <div class="mt-8 bg-brand-card/80 backdrop-blur-sm overflow-hidden shadow-lg sm:rounded-lg border border-white/10">
                <div class="px-6 py-4 border-b border-gray-700">
                    <h3 class="text-lg font-medium text-gray-100">Recent Reports</h3>
                </div>
                <div class="p-6 text-gray-100">
                    <div class="overflow-x-auto">
                        <table class="min-w-full divide-y divide-gray-700">
                            <thead>
                                <tr>
                                    <th class="px-6 py-3 bg-brand-darker text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Type</th>
                                    <th class="px-6 py-3 bg-brand-darker text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Time</th>
                                    <th class="px-6 py-3 bg-brand-darker text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Status</th>
                                </tr>
                            </thead>
                            <tbody class="bg-transparent divide-y divide-gray-700">
                                @forelse($recentReports as $report)
                                <tr>
                                    <td class="px-6 py-4 whitespace-nowrap">{{ $report->incident_type }}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-gray-400">{{ $report->created_at->diffForHumans() }}</td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full 
                                            {{ $report->status === 'Active' ? 'bg-yellow-900 text-yellow-200' : '' }}
                                            {{ $report->status === 'Verified' ? 'bg-green-900 text-green-200' : '' }}
                                            {{ $report->status === 'False' ? 'bg-red-900 text-red-200' : '' }}">
                                            {{ $report->status }}
                                        </span>
                                    </td>
                                </tr>
                                @empty
                                <tr>
                                    <td colspan="3" class="px-6 py-4 text-center text-gray-500">No recent reports found.</td>
                                </tr>
                                @endforelse
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</x-admin-layout>
