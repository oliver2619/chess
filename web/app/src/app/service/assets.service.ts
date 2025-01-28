import {Injectable} from '@angular/core';
import {FigureColor} from '../state/figure-color';
import {FigureType} from '../state/figure-type';

class Asset {

  private readonly promise: Promise<SVGSVGElement>;

  constructor(private readonly url: string) {
    this.promise = fetch(this.url, {method: 'get'})
      .then(response => response.text())
      .then(text => this.createSvg(text));
  }

  get(): Promise<SVGSVGElement> {
    return this.promise;
  }

  private createSvg(svg: string): SVGSVGElement {
    const el = document.createElement('div');
    el.innerHTML = svg;
    return el.firstElementChild as SVGSVGElement;
  }
}

const FIGURE_COLORS_LETTER = Object.freeze(['w', 'b']);
const FIGURE_TYPES_LETTER = Object.freeze(['p', 'n', 'b', 'r', 'q', 'k']);

@Injectable({
  providedIn: 'root'
})
export class AssetsService {

  private readonly assetsByUrl = new Map<string, Asset>();

  get(url: string): Promise<SVGSVGElement> {
    const found = this.assetsByUrl.get(url);
    if (found == undefined) {
      const newAsset = new Asset(url);
      this.assetsByUrl.set(url, newAsset);
      return newAsset.get();
    } else {
      return found.get();
    }
  }

  getFigureImageTitle(color: FigureColor, type: FigureType): string {
    return `${FIGURE_COLORS_LETTER[color]}${FIGURE_TYPES_LETTER[type]}`;
  }
}
