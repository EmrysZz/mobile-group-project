<?php

namespace App\Filament\Resources\Shelters\Schemas;

use Filament\Forms\Form;

class ShelterForm
{
    public static function configure(Form $form): Form
    {
        return $form
            ->schema([
                \Filament\Forms\Components\TextInput::make('name')
                    ->required()
                    ->maxLength(255),
                \Filament\Forms\Components\Textarea::make('description')
                    ->required()
                    ->columnSpanFull(),
                \Filament\Forms\Components\TextInput::make('latitude')
                    ->numeric()
                    ->required(),
                \Filament\Forms\Components\TextInput::make('longitude')
                    ->numeric()
                    ->required(),
            ]);
    }
}
