<x-admin-layout>
    <x-slot name="header">
        <h2 class="font-semibold text-xl text-brand-orange leading-tight">
            {{ __('Manage News') }}
        </h2>
    </x-slot>

    <div class="py-12">
        <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
            <div class="flex justify-end mb-4">
                <a href="{{ route('admin.news.create') }}" class="bg-gradient-to-r from-brand-red to-brand-orange hover:opacity-90 text-white font-bold py-2 px-4 rounded shadow-lg">
                    Publish News
                </a>
            </div>

            <div class="bg-brand-card/80 backdrop-blur-sm overflow-hidden shadow-lg sm:rounded-lg border border-white/10">
                <div class="p-6 text-gray-100">
                    <table class="min-w-full divide-y divide-gray-700">
                        <thead>
                            <tr>
                                <th class="px-6 py-3 bg-brand-darker text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Title</th>
                                <th class="px-6 py-3 bg-brand-darker text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Published At</th>
                                <th class="px-6 py-3 bg-brand-darker text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Actions</th>
                            </tr>
                        </thead>
                        <tbody class="bg-transparent divide-y divide-gray-700">
                            @foreach ($news as $item)
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap">{{ $item->title }}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-gray-400">{{ $item->published_at ? $item->published_at->format('Y-m-d H:i') : 'Draft' }}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                    <form action="{{ route('admin.news.destroy', $item) }}" method="POST" class="inline-block">
                                        @csrf
                                        @method('DELETE')
                                        <button type="submit" class="text-red-500 hover:text-red-300" onclick="return confirm('Are you sure?')">Delete</button>
                                    </form>
                                </td>
                            </tr>
                            @endforeach
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</x-admin-layout>
