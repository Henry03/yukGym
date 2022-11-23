<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\User;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\Session;
use Laravel\Passport\Token;
use Laravel\Passport\HasApiTokens;

class AuthController extends Controller
{
    public function register(Request $request){
        $registrationData = $request->all();    // Mengambil seluruh data input dan menyimpan dalam variabel registratinoData
         $validate = Validator::make($registrationData, [
            'username' => 'required',
            'notelp' => 'required',
            'email' => 'required|email:rfc,dns|unique:users',
            'birthdate' => 'required',
            'password' => 'required',
            // 'image' => 'mimes:jpeg,jpg,png,gif,svg|max:2048'
         ]);    // rule validasi input saat register

         if($validate->fails())    // Mengecek apakah inputan sudah sesuai dengan rule validasi
            return response(['message' => $validate->errors()], 400);   // Mengembalikan error validasi input

        $registrationData['password'] = bcrypt($request->password); // Untuk meng-enkripsi password
        // $uploadFolder = 'public';
        // $image = $request->file('image');
        // $image_uploaded_path = $image->store($uploadFolder, 'public');
        // $registrationData['image'] = $image_uploaded_path;
        $user = User::create($registrationData);    // Membuat user baru

        return response([
            'message' => 'Register Success',
            'user' => $user
        ], 200); // return data user dalam bentuk json

    }

    public function login (Request $request){
        $loginData = $request->all();
 
        $validate = Validator::make($loginData, [
            'username' => 'required',
            'password' => 'required'
        ]);

        if($validate->fails())    // Mengecek apakah inputan sudah sesuai dengan rule validasi
            return response(['message' => $validate->errors()], 400);   // Mengembalikan error validasi input

        if(!Auth::attempt($loginData))    // Mengecek apakah inputan sudah sesuai dengan rule validasi
            return response(['message' => 'Invalid Credentials', 'data'=>$loginData], 401);   // Mengembalikan error gagal login

        $user = Auth::user();
        $token = $user->createToken('Authentication Token')->accessToken;   //generate token

        return response([
            'message' => 'Authenticated',
            'user' => $user,
            'token_type' => 'Bearer',
            'access_token' => $token
        ]); // return data user dan token dalam bentuk json

    }

    public function logout(Request $request){
        $user = Auth::user()->token();
        $dataUser = Auth::user();
        $user->revoke();
        return response([
            'message' => 'Logout Succes',
            'user' => $dataUser
        ]);
    }

    public function authCheck(){
        if(auth()->user()){
            return response([
                'message' => 'Authenticated',
                'data' => auth()->user()
            ], 200);
        }else{
            return response(
                [
                    'message' => 'Unauthenticated',
                    'data' => null
                ], 401
            );
        }
    }
}
