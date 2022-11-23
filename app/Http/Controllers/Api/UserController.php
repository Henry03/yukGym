<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\User;
use Illuminate\Support\Facades\Validator;

class UserController extends Controller
{
    public function index(){
        // get posts
        $user = User::all();
        
        if(count($user) > 0){
            return response([
    
                'data' => $user
            ], 200);
        }// return data semua user dalam bentuk json

        return response([
            'message' => 'Empty',
            'data' => null
        ], 400); 
    }

    public function store(Request $request){
        $storeData = $request->all();
        $validate = Validator::make($storeData, [
            'username' => 'required',
            'notelp' => 'required',
            'email' => 'required',
            'birthdate' => 'required',
            'password' => 'required',
        ]);

        if($validate->fails())
            return response(['message' => $validate->errors()], 400);

        $user = User::create($storeData); // Membuat sebuah data user
        return response([
            'message' => 'Add User Success',
            'data' => $user
        ], 200);
    }

    public function show($id)   // Method search atau menampilkan sebuah data user
    {
        $user = User::find($id); // Mencari data user berdasarkan id
        
        if(!is_null($user)){
            return response([
                'message' => 'Retrieve User Success',
                'data' => $user
            ], 200);
        }
        
        return response([
            'message' => 'User Not Found',
            'data' => null
        ], 404);
    }

    public function update(Request $request, $id)   // Method update atau mengubah sebuah data user
    {
        $user = User::find($id);

        if(is_null($user)){
            return response([
                'message' => 'User Not Found',
                'data' => null
            ], 404);
        }

        $updateData = $request->all();
        $validate = Validator::make($updateData, [
            'username' => 'required',
            'notelp' => 'required',
            'email' => 'required',
            'birthdate' => 'required'
        ]);

        if($validate->fails())
            return response(['message' => $validate->errors()], 400);

        $user->username = $updateData['username'];
        $user->notelp = $updateData['notelp'];
        $user->email = $updateData['email'];
        $user->birthdate = $updateData['birthdate'];

        if($user->save()){
            return response([
                'message' => 'Update User Success',
                'data' => $user
            ], 200);
        }

        return response([
            'message' => 'Update User Failed',
            'data' => null
        ], 400);
    }

    public function destroy($id)
    {
        $user = User::find($id);

        if(is_null($user)){
            return response([
                'message' => 'User Not Found',
                'data' => null
            ], 404);
        }

        if($user->delete()){
            return response([
                'message' => 'Delete User Success',
                'data' => $user
            ], 200);
        }

        return response([
            'message' => 'Delete User Failed',
            'data' => null
        ], 400);
    }

    
}
