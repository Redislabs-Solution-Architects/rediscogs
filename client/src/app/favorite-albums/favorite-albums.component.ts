import { Component, OnInit } from '@angular/core';
import { StompService, StompConfig } from '@stomp/ng2-stompjs';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-favorite-albums',
  templateUrl: './favorite-albums.component.html',
  styleUrls: ['./favorite-albums.component.css']
})
export class FavoriteAlbumsComponent implements OnInit {

  API_URL = '/api/';

  private stomp: any;
  private stompService: StompService;
  private history: any;

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.http.get(this.API_URL + 'likes').subscribe((history: any) => this.history = history);
    this.http.get(this.API_URL + 'stomp-config').subscribe((stomp: any) => this.stomp = stomp);
    const stompConfig: StompConfig = {
      url: 'ws://' + this.stomp.host + ':' + this.stomp.port + this.stomp.endpoint,
      headers: {
        login: '',
        passcode: ''
      },
      heartbeat_in: 0,
      heartbeat_out: 20000,
      reconnect_delay: 5000,
      debug: true
    };
    this.stompService = new StompService(stompConfig);
    this.stompService.subscribe(this.stomp.likesTopic).subscribe(like => this.history.likes.unshift(JSON.parse(like.body)));
  }

}
