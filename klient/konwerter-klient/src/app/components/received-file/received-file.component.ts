import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-received-file',
  templateUrl: './received-file.component.html',
  styleUrls: ['./received-file.component.css']
})
export class ReceivedFileComponent implements OnInit {

  @Input() htitle: string;
  @Input() resT: string;
  @Input() iR: boolean;
  constructor() { }

  ngOnInit(): void {
  }

}
