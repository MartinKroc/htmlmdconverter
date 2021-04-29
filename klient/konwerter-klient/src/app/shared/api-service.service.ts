import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiServiceService {
  private baseUrl = 'http://localhost:8080/api';
  constructor(private http: HttpClient) { }

  uploadFile(file: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    const req = new HttpRequest('POST', `${this.baseUrl}/convert/upload`, formData, {
      reportProgress: true,
      responseType: 'json'
    });

    return this.http.request(req);
  }

  getFiles(): Observable<any> {
    return this.http.get(`${this.baseUrl}/convert/files`);
  }

  convertHTMLToMD(): Observable<any> {
    return this.http.get(`${this.baseUrl}/convert/conv/html`, {responseType: 'blob' as 'json'});
  }

  convertMDToHTML(): Observable<any> {
    return this.http.get(`${this.baseUrl}/convert/conv/md`, {responseType: 'blob' as 'json'});
  }
  convertMDToXML(): Observable<any> {
    return this.http.get(`${this.baseUrl}/convert/conv/xml`, {responseType: 'blob' as 'json'});
  }
}
