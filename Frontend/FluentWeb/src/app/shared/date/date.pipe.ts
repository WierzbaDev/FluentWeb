import {Pipe, PipeTransform} from '@angular/core';
import {format} from 'date-fns';
import {pl} from 'date-fns/locale';

@Pipe({
  name: 'customDate',
  standalone: true
})
export class CustomDatePipe implements PipeTransform {
  transform(value: Date | string | number): string {
    return format(new Date(value), "d MMMM yyyy, EEEE", { locale: pl });
  }
}
