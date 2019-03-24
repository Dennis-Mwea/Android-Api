<?php

namespace App\Http\Controllers\Api\Auth;

use Illuminate\Http\Request;
use Laravel\Passport\Client;
use Illuminate\Support\Facades\DB;
use App\Http\Controllers\Controller;
use Auth;
use App\Traits\IssueTokenTrait;

class LoginController extends Controller
{
    use IssueTokenTrait;

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
     * Login an existing user through api.
     *
     * @param Request $request
     *
     * @return passport access token
     */
    public function login(Request $request)
    {
        $this->validate($request, [
            'email' => 'required',
            'password' => 'required',
        ]);

        return $this->issueToken($request, 'password');
    }

    /**
     * Refresh authenticated user's token through api.
     *
     * @param Request $request
     *
     * @return passport access token
     */
    public function refresh(Request $request)
    {
        $this->validate($request, [
            'refresh_token' => 'required',
        ]);

        return $this->issueToken($request, 'refresh_token');
    }

    /**
     * Logout an authenticated user through api.
     *
     * @param Request $request
     */
    public function logout(Request $request)
    {
        $accessToken = Auth::user()->token();

        DB::table('oauth_refresh_tokens')
            ->where('access_token_id', $accessToken->id)
            ->update(['revoked' => true]);

        $accessToken->revoke();

        return response()->json([], 204);
    }
}
