class PdfControllers < Sinatra::Base

    configure do
      set :views, "app/views"
      set :public_dir, "public"
    end
  
    get '/' do
      erb :'index'
    end
  
    post '/' do
      url = ERB.new(params[:url]).result(binding)
      if url =~ /^https?:\/\//i
        filename = Array.new(32){rand(36).to_s(36)}.join + '.pdf'
        path = 'pdf/' + filename
  
        begin
             PDFKit.new(url).to_file(path)
            cmd = `exiftool -overwrite_original -all= -creator="Generated by pdfkit v0.8.6" -xmptoolkit= #{path}`
            send_file path, :disposition => 'attachment'
        rescue
             @msg = 'Cannot load remote URL!'
        end
  
      else
          @msg = 'You should provide a valid URL!'
      end
      erb :'index'
    end
  end
  