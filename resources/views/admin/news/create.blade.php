<x-admin-layout>
    <x-slot name="header">
        <h2 class="font-semibold text-xl text-brand-orange leading-tight">
            {{ __('Publish News') }}
        </h2>
    </x-slot>

    <div class="py-12">
        <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
            <div class="bg-brand-card/80 backdrop-blur-sm overflow-hidden shadow-lg sm:rounded-lg border border-white/10">
                <div class="p-6 text-gray-100">
                    <form method="POST" action="{{ route('admin.news.store') }}">
                        @csrf

                        <!-- Title -->
                        <div>
                            <x-input-label for="title" :value="__('Title')" class="text-gray-300" />
                            <x-text-input id="title" class="block mt-1 w-full bg-gray-900 border-gray-700 text-gray-100 focus:ring-brand-orange focus:border-brand-orange" type="text" name="title" :value="old('title')" required autofocus />
                            <x-input-error :messages="$errors->get('title')" class="mt-2" />
                        </div>

                        <!-- Content -->
                        <div class="mt-4">
                            <x-input-label for="content" :value="__('Content')" class="text-gray-300" />
                            <textarea id="content" name="content" class="block mt-1 w-full bg-gray-900 border-gray-700 text-gray-100 focus:ring-brand-orange focus:border-brand-orange rounded-md shadow-sm" rows="5" required>{{ old('content') }}</textarea>
                            <x-input-error :messages="$errors->get('content')" class="mt-2" />
                        </div>

                        <div class="flex items-center justify-end mt-4">
                            <x-primary-button class="ml-4 bg-gradient-to-r from-brand-red to-brand-orange border-none hover:opacity-90">
                                {{ __('Publish') }}
                            </x-primary-button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</x-admin-layout>
