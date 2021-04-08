import {Component, DoCheck, OnInit} from '@angular/core';
import {ApiServiceService} from '../../shared/api-service.service';
import {Observable} from 'rxjs';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class IndexComponent implements OnInit, DoCheck {
  formats: Format[] = [
    {value: 'html-0', viewValue: 'HTML'},
    {value: 'md-1', viewValue: 'MD'}
  ];
  fileInfos?: Observable<any>;
  convertButton = false;
  constructor(private apiService: ApiServiceService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.fileInfos = this.apiService.getFiles();
  }

  ngDoCheck(): void {

  }

  convert() {
    this.convertButton = true;
  }
  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 2000,
    });
  }
}
interface Format {
  value: string;
  viewValue: string;
}
