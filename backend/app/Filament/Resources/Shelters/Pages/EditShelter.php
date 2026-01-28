<?php

namespace App\Filament\Resources\Shelters\Pages;

use App\Filament\Resources\Shelters\ShelterResource;
use Filament\Actions\DeleteAction;
use Filament\Resources\Pages\EditRecord;

class EditShelter extends EditRecord
{
    protected static string $resource = ShelterResource::class;

    protected function getHeaderActions(): array
    {
        return [
            DeleteAction::make(),
        ];
    }
}
