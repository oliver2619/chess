import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  constructor() {
  }

  download(data: {data: string; mimeType: string; filename: string}) {
    const anchor = document.createElement('a');
    anchor.download = data.filename;
    anchor.type = data.mimeType;
    anchor.href = `data:${data.mimeType};base64,${btoa(data.data)}`;
    anchor.style.display = 'none';
    anchor.style.visibility = 'hidden';
    anchor.style.position = 'absolute';
    document.documentElement.append(anchor);
    anchor.click();
    anchor.remove();
  }

  upload(accept: string): Promise<string> {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = accept;
    input.style.display = 'none';
    input.style.visibility = 'hidden';
    input.style.position = 'absolute';
    document.documentElement.append(input);
    let res: (result: string) => void;
    let rej: (reason: any) => void;
    const promise = new Promise<string>((resolve, reject) => {
      res = resolve;
      rej = reject;
    });
    input.onchange = () => {
      input.remove();
      if (input.files != null && input.files.length === 1) {
        const file = input.files.item(0);
        if (file != null) {
          file.text().then(it => res(it)).catch(it => rej(it));
        }
      }
    };
    input.oncancel = () => {
      input.remove();
    };
    input.click();
    return promise;
  }
}
