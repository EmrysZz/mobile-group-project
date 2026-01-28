<x-admin-layout>
    <x-slot name="header">
        <h2 class="font-semibold text-xl text-brand-orange leading-tight">
            {{ __('User Reports') }}
        </h2>
    </x-slot>

    <div class="py-12">
        <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
            <div class="bg-brand-card/80 backdrop-blur-sm overflow-hidden shadow-lg sm:rounded-lg border border-white/10">
                <div class="p-6 text-gray-100">
                    <table class="min-w-full divide-y divide-gray-700">
                        <thead>
                            <tr>
                                <th class="px-6 py-3 bg-brand-darker text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Type</th>
                                <th class="px-6 py-3 bg-brand-darker text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Time</th>
                                <th class="px-6 py-3 bg-brand-darker text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Reporter</th>
                                <th class="px-6 py-3 bg-brand-darker text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Status</th>
                                <th class="px-6 py-3 bg-brand-darker text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Actions</th>
                            </tr>
                        </thead>
                        <tbody class="bg-transparent divide-y divide-gray-700">
                            @foreach ($reports as $report)
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap">{{ $report->incident_type }}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-gray-400">{{ $report->report_time }}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-gray-300">{{ $report->user_name }}</td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full 
                                        {{ $report->status === 'Active' ? 'bg-yellow-900 text-yellow-200' : '' }}
                                        {{ $report->status === 'Verified' ? 'bg-green-900 text-green-200' : '' }}
                                        {{ $report->status === 'False' ? 'bg-red-900 text-red-200' : '' }}
                                        {{ $report->status === 'Resolved' ? 'bg-gray-700 text-gray-300' : '' }}">
                                        {{ $report->status }}
                                    </span>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                    <a href="{{ route('admin.reports.show', $report) }}" class="text-brand-orange hover:text-white">View Details</a>
                                </td>
                            </tr>
                            @endforeach
                        </tbody>
                    </table>
                    <div class="mt-4">
                        {{ $reports->links() }}
                    </div>
                </div>
            </div>
        </div>
    </div>
</x-admin-layout>
