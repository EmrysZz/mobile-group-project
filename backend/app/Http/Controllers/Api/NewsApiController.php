<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\News;
use Illuminate\Http\Request;

class NewsApiController extends Controller
{
    /**
     * List all news items.
     */
    public function index()
    {
        return News::orderBy('published_at', 'desc')->get();
    }
}
