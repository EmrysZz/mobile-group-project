<?php

namespace App\Filament\Widgets;

use Filament\Widgets\StatsOverviewWidget as BaseWidget;
use Filament\Widgets\StatsOverviewWidget\Stat;

class StatsOverview extends BaseWidget
{
    protected function getStats(): array
    {
        return [
            Stat::make('Total Reports', \App\Models\Report::count())
                ->description('Total incidents reported')
                ->descriptionIcon('heroicon-m-document-text')
                ->color('primary'),
            Stat::make('Active Shelters', \App\Models\Shelter::count())
                ->description('Total safe zones')
                ->descriptionIcon('heroicon-m-home-modern')
                ->color('success'),
            Stat::make('Reports Today', \App\Models\Report::whereDate('created_at', now())->count())
                ->description('New incidents today')
                ->descriptionIcon('heroicon-m-arrow-trending-up')
                ->color('danger'),
        ];
    }
}
