<?php

namespace App\Http\Controllers\Api\Auth;

use App\User;
use Illuminate\Http\Request;
use Laravel\Passport\Client;
use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\Hash;

class RegisterController extends Controller
{
    /**
     * Passport client.
     *
     * @var
     */
    private $client;

    /**
     * Initialize passport client.
     */
    public function __construct()
    {
        $this->client = Client::find(1);
    }

    /**
     * Register a new user through api.
     *
     * @param Request $request
     *
     * @return passport access token
     */
    public function register(Request $request)
    {
        $this->validate($request, [
            'name' => 'required|string',
            'email' => 'required|email|unique:users,email',
            'password' => 'required|min:8|confirmed',
        ]);

        $user = User::create([
            'name' => $request->name,
            'email' => $request->email,
            'password' => Hash::make($request->password),
        ]);

        return $this->issueToken($request, 'password');
    }
}
