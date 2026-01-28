<x-admin-layout>
    <x-slot name="header">
        <h2 class="font-semibold text-xl text-brand-orange leading-tight">
            {{ __('Edit Shelter') }}
        </h2>
    </x-slot>

    <div class="py-12">
        <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
            <div class="bg-brand-card/80 backdrop-blur-sm overflow-hidden shadow-lg sm:rounded-lg border border-white/10">
                <div class="p-6 text-gray-100">
                    <form method="POST" action="{{ route('admin.shelters.update', $shelter) }}">
                        @csrf
                        @method('PUT')

                        <!-- Name -->
                        <div>
                            <x-input-label for="name" :value="__('Name')" class="text-gray-300" />
                            <x-text-input id="name" class="block mt-1 w-full bg-gray-900 border-gray-700 text-gray-100 focus:ring-brand-orange focus:border-brand-orange" type="text" name="name" :value="old('name', $shelter->name)" required autofocus />
                            <x-input-error :messages="$errors->get('name')" class="mt-2" />
                        </div>

                        <!-- Description -->
                        <div class="mt-4">
                            <x-input-label for="description" :value="__('Description')" class="text-gray-300" />
                            <textarea id="description" name="description" class="block mt-1 w-full bg-gray-900 border-gray-700 text-gray-100 focus:ring-brand-orange focus:border-brand-orange rounded-md shadow-sm" rows="3" required>{{ old('description', $shelter->description) }}</textarea>
                            <x-input-error :messages="$errors->get('description')" class="mt-2" />
                        </div>

                        <!-- Latitude -->
                        <div class="mt-4">
                            <x-input-label for="latitude" :value="__('Latitude')" class="text-gray-300" />
                            <x-text-input id="latitude" class="block mt-1 w-full bg-gray-900 border-gray-700 text-gray-100 focus:ring-brand-orange focus:border-brand-orange" type="text" name="latitude" :value="old('latitude', $shelter->latitude)" required />
                            <x-input-error :messages="$errors->get('latitude')" class="mt-2" />
                        </div>

                        <!-- Longitude -->
                        <div class="mt-4">
                            <x-input-label for="longitude" :value="__('Longitude')" class="text-gray-300" />
                            <x-text-input id="longitude" class="block mt-1 w-full bg-gray-900 border-gray-700 text-gray-100 focus:ring-brand-orange focus:border-brand-orange" type="text" name="longitude" :value="old('longitude', $shelter->longitude)" required />
                            <x-input-error :messages="$errors->get('longitude')" class="mt-2" />
                        </div>

                        <div class="flex items-center justify-end mt-4">
                            <x-primary-button class="ml-4 bg-gradient-to-r from-brand-red to-brand-orange border-none hover:opacity-90">
                                {{ __('Update Shelter') }}
                            </x-primary-button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</x-admin-layout>
