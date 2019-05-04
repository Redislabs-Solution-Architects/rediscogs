import { Component, OnInit } from '@angular/core';
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

  private results: Observable<any>;
  private artists: Observable<any>;
  private searchField: FormControl;
  private artistField: FormControl;

  constructor(private searchService: SearchService) { }

  ngOnInit() {
    this.searchField = new FormControl();
    this.artistField = new FormControl();
    this.artistField.valueChanges.pipe(
      debounceTime(300)
    ).subscribe(prefix => this.searchService.suggestArtists(this.artistField.value).subscribe(data => { this.artists = data; }));
  }

  artistSelected(artist: any) {
    this.searchField.setValue('@artistId:{' + artist.id + '} ');
  }

  favorite(album: any) {
    this.searchService.favoriteAlbum(album);
    album.favorite = true;
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
