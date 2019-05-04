import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  constructor(private http: HttpClient) { }

  suggestArtists(prefix: string): Observable<any> {
    let params = new HttpParams();
    if (prefix != null) {
      params = params.set('prefix', prefix);
    }
    return this.http.get('/suggest-artists', { params });
  }

  searchAlbums(artistId: string, query: string): Observable<any> {
    let params = new HttpParams();
    if (artistId != null) {
      params = params.set('artistId', artistId);
    }
    if (query != null) {
      params = params.set('query', query);
    }
    return this.http.get('/search-albums', { params });
  }

  favoriteAlbum(album: any) {
    const options = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json'
      })
    };
    this.http.post('/favorite-album', album, options).subscribe(
      (val) => {
      },
      response => {
          console.log('POST call in error', response);
      },
      () => {
      });
  }
}
