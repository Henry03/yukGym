<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Validation\Rule;
use Illuminate\Support\Facades\Validator;
use App\Models\History;

class HistoryController extends Controller
{

    public function index($user_id){
        // get posts
        $history = History::where('user_id', $user_id)->latest('tanggal')->get();
        
        if(count($history) > 0){
            return response([
                'message' => 'Retrieve All Success',
                'data' => $history
            ], 200);
        }// return data semua history dalam bentuk json

        return response([
            'message' => 'Empty',
            'data' => null
        ], 400); 
    }

    public function last($user_id){
        // get posts
        $history = History::where('user_id', $user_id)->latest('tanggal')->first();
        
        if($history != null){
            return response([
                'message' => 'Retrieve All Success',
                'data' => $history
            ], 200);
        }// return data semua history dalam bentuk json

        return response([
            'message' => 'Empty',
            'data' => null
        ], 400); 
    }

    public function store(Request $request){
        $storeData = $request->all();
        $validate = Validator::make($storeData, [
            'berat_badan' => 'required',
            'aktivitas' => 'required',
            'lama_latihan' => 'required',
            'tanggal' => 'required',
            'user_id' => 'required',
        ]);

        if($validate->fails())
            return response(['message' => $validate->errors()], 400);

        $history = History::create($storeData); // Membuat sebuah data history
        return response([
            'message' => 'Add History Success',
            'data' => $history
        ], 200);
    }

    public function show($id)   // Method search atau menampilkan sebuah data history
    {
        $history = History::find($id); // Mencari data history berdasarkan id
        
        if(!is_null($history)){
            return response([
                'message' => 'Retrieve History Success',
                'data' => $history
            ], 200);
        }
        
        return response([
            'message' => 'History Not Found',
            'data' => null
        ], 404);
    }

    public function update(Request $request, $id)   // Method update atau mengubah sebuah data history
    {
        $history = History::find($id);

        if(is_null($history)){
            return response([
                'message' => 'History Not Found',
                'data' => null
            ], 404);
        }

        $updateData = $request->all();
        $validate = Validator::make($updateData, [
            'berat_badan' => 'required',
            'aktivitas' => 'required',
            'lama_latihan' => 'required',
            'tanggal' => 'required',
            'image' => 'required',
        ]);

        if($validate->fails())
            return response(['message' => $validate->errors()], 400);

        $history->berat_badan = $updateData['berat_badan'];
        $history->aktivitas = $updateData['aktivitas'];
        $history->lama_latihan = $updateData['lama_latihan'];
        $history->tanggal = $updateData['tanggal'];
        $history->image = $updateData['image'];

        if($history->save()){
            return response([
                'message' => 'Update History Success',
                'data' => $history
            ], 200);
        }

        return response([
            'message' => 'Update History Failed',
            'data' => null
        ], 400);
    }

    public function destroy($id)
    {
        $history = History::find($id);

        if(is_null($history)){
            return response([
                'message' => 'History Not Found',
                'data' => null
            ], 404);
        }

        if($history->delete()){
            return response([
                'message' => 'Delete History Success',
                'data' => $history
            ], 200);
        }

        return response([
            'message' => 'Delete History Failed',
            'data' => null
        ], 400);
    }
}
