<?php
namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Request;

class LoginController extends AbstractController
{
    public function login(Request $request)
    {
        if ($request->get('username') && $request->get('password'))
        {
            if (
                $request->get('username') == $_SERVER['ADMIN_USENRAME'] &&
                $request->get('password') == $_SERVER['ADMIN_PASSWORD']
            )
            {
                $this->get('session')->set('loggedin', true);
                return $this->redirect('/admin/');
            }
            return $this->redirect('/?msg=login failed');
        }
        return $this->redirect('/?msg=Please login first!');
    }

}