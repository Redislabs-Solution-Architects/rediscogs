import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { ReactiveFormsModule, FormControl, FormsModule } from '@angular/forms';
import {
  map,
  debounceTime,
  distinctUntilChanged,
  switchMap,
  tap
} from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  private userField: FormControl;

  title = 'ReDiscogs';

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.userField = new FormControl();
    this.userField.valueChanges.pipe(debounceTime(300)).subscribe(name => this.setUsername(this.userField.value));
  }

  setUsername(name: string) {
    const options = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json'
      })
    };
    this.http.post('/username', name, options).subscribe(
      (val) => {
      },
      response => {
          console.log('POST call in error', response);
      },
      () => {
      });
    console.log(name);
  }
}
