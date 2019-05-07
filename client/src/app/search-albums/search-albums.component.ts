import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { SearchService } from '../search.service';
import { Observable } from 'rxjs';
import { ReactiveFormsModule, FormControl, FormsModule } from '@angular/forms';
import {
  map,
  debounceTime,
  distinctUntilChanged,
  switchMap,
  tap
} from 'rxjs/operators';

@Component({
  selector: 'app-search-albums',
  templateUrl: './search-albums.component.html',
  styleUrls: ['./search-albums.component.css']
})
export class SearchAlbumsComponent implements OnInit {
  API_URL = '/api/';
  title = 'ReDiscogs';
  private userField: FormControl;
  private results: Observable<any>;
  private artists: Observable<any>;
  private searchField: FormControl;
  private artistField: FormControl;

  constructor(private http: HttpClient, private searchService: SearchService) { }

  ngOnInit() {
    this.userField = new FormControl();
    this.userField.setValue('Anonymous Coward');
    this.getUsername().subscribe((res) => {
      if (res) {
        this.userField.setValue(res.name);
      }
    });
    this.userField.valueChanges.pipe(debounceTime(300)).subscribe(name => this.setUsername(this.userField.value));
    this.searchField = new FormControl();
    this.artistField = new FormControl();
    this.artistField.valueChanges.pipe(
      debounceTime(300)
    ).subscribe(prefix => this.searchService.suggestArtists(this.artistField.value).subscribe(data => { this.artists = data; }));
  }

  getUsername(): Observable<any> {
    return this.http.get(this.API_URL + 'user');
  }

  setUsername(username: string) {
    const options = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json'
      })
    };
    const user = {
      name: username
    };
    this.http.post(this.API_URL + 'user', user, options).subscribe(
      (val) => {
      },
      response => {
          console.log('POST call in error', response);
      },
      () => {
      });
  }

  artistSelected(artist: any) {
    this.searchField.setValue('@artistId:{' + artist.id + '} ');
  }

  like(album: any) {
    this.searchService.likeAlbum(album);
    album.like = true;
  }

  displayFn(artist: any) {
    if (artist) { return artist.name; }
  }

  search() {
    let artistId = null;
    if (this.artistField.value != null) {
      artistId = this.artistField.value.id;
    }
    this.results = this.searchService.searchAlbums(artistId, this.searchField.value);
  }

}
