import { Component, OnInit } from '@angular/core';
import {ApiServiceService} from '../../shared/api-service.service';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class IndexComponent implements OnInit {
  fileInfos?: Observable<any>;
  constructor(private apiService: ApiServiceService) { }

  ngOnInit(): void {
    this.fileInfos = this.apiService.getFiles();
  }

}
