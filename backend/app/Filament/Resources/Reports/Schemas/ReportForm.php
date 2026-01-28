<?php

namespace App\Filament\Resources\Reports\Schemas;

use Filament\Forms\Form;

class ReportForm
{
    public static function configure(Form $form): Form
    {
        return $form
            ->schema([
                \Filament\Forms\Components\TextInput::make('incident_type')
                    ->disabled(),
                \Filament\Forms\Components\Textarea::make('description')
                    ->disabled()
                    ->columnSpanFull(),
                \Filament\Forms\Components\TextInput::make('latitude')
                    ->disabled(),
                \Filament\Forms\Components\TextInput::make('longitude')
                    ->disabled(),
            ]);
    }
}
