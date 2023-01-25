<?php
namespace App\Controller;
use App\Service\TemplateGenerator;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Request;

class AdminController extends AbstractController
{

    public function adminIndex(Request $request)
    {
        if (!$this->get('session')->get('loggedin'))
        {
            return $this->redirect('/?msg=please login first');
        }

        return $this->render('site/admin.html');
    }

    public function exportTemplate(Request $request)
    {
        if (!$this->get('session')->get('loggedin'))
        {
            return $this->redirect('/?msg=please login first');
        }

        $templateGenerator = new TemplateGenerator(
            $request->get('template-page'),
            $request->get('campaign'),
            $request->get('log-title'),
            $request->get('slack-url'),
            $request->get('redirect-url'),
            $this->get('twig')
        );

        if (!$templateGenerator->verifyTemplate())
        {
            return $this->redirect('/admin/?msg=Invalid Template Selected!');
        }

        $templateGenerator->generateIndex();

        if ($templateGenerator->createArchive())
        {
            return $this->redirect('/admin/?export=true');
        }

        return $this->redirect('/admin/?msg=Template Export Failed!');
    }

    public function logout(Request $request)
    {
        $request->getSession()->invalidate();;
        return $this->redirect('/');
    }


}