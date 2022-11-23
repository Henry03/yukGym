<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

Route::post('register', 'Api\AuthController@register');
Route::post('login', 'Api\AuthController@login');



Route::group(['middleware' => 'auth:api'], function(){
    
    Route::get('user/{id}', 'Api\UserController@show');
    Route::get('user', 'Api\UserController@index');
    Route::post('user', 'Api\UserController@store');
    Route::put('user/{id}', 'Api\UserController@update');
    Route::delete('user/{id}', 'Api\UserController@destroy');

    Route::post('logout', 'Api\AuthController@logout');
    Route::post('auth', 'Api\AuthController@authCheck');

    Route::get('history/{id}', 'Api\HistoryController@index');
    Route::get('historyLast/{id}', 'Api\HistoryController@last');
    Route::get('historyDetail/{id}', 'Api\HistoryController@show');
    Route::post('history', 'Api\HistoryController@store');
    Route::put('history/{id}', 'Api\HistoryController@update');
    Route::delete('history/{id}', 'Api\HistoryController@destroy');
});
