import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'time'
})
export class TimePipe implements PipeTransform {

  transform(value: unknown, ...args: unknown[]): unknown {
    if (typeof value === 'number') {
      return this.format(Math.max(0, value));
    } else {
      return value;
    }
  }

  private format(value: number): string {
    const h = Math.floor(value / 3600);
    const rm = value - h * 3600;
    const m = Math.floor(rm / 60);
    const s = Math.ceil(rm - m * 60);
    return `${h}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
  }

}
