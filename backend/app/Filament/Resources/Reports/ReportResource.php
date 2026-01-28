<?php

namespace App\Filament\Resources\Reports;

use App\Filament\Resources\Reports\Pages\CreateReport;
use App\Filament\Resources\Reports\Pages\EditReport;
use App\Filament\Resources\Reports\Pages\ListReports;
use App\Filament\Resources\Reports\Schemas\ReportForm;
use App\Filament\Resources\Reports\Tables\ReportsTable;
use App\Models\Report;
use BackedEnum;
use Filament\Forms\Form;
use Filament\Resources\Resource;
use Filament\Support\Icons\Heroicon;
use Filament\Tables\Table;
use Filament\Tables\Columns\TextColumn;

class ReportResource extends Resource
{
    protected static ?string $model = Report::class;

    protected static ?string $navigationIcon = 'heroicon-o-rectangle-stack';

    public static function form(Form $form): Form
    {
        return ReportForm::configure($form);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                TextColumn::make('user_name')->label('Reported By')->searchable(),
                TextColumn::make('incident_type')
                    ->badge()
                    ->colors([
                        'danger' => 'Request Help',
                        'warning' => 'Flood',
                        'success' => 'Safe',
                        'info' => 'Traffic',
                    ]),
                TextColumn::make('description')->limit(50),
                TextColumn::make('user_agent')->label('Device Info')->toggleable(),
                TextColumn::make('latitude'),
                TextColumn::make('longitude'),
                TextColumn::make('report_time')->dateTime()->sortable(),
            ]);
    }

    public static function getRelations(): array
    {
        return [
            //
        ];
    }

    public static function getPages(): array
    {
        return [
            'index' => ListReports::route('/'),
            'create' => CreateReport::route('/create'),
            'edit' => EditReport::route('/{record}/edit'),
        ];
    }
}
