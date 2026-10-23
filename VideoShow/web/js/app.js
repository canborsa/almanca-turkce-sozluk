// VideoShow Web Application JS
const API_BASE_URL = '/api/v1';

class VideoShowApp {
  constructor() {
    this.token = localStorage.getItem('access_token') || null;
    this.user = JSON.parse(localStorage.getItem('user_data') || 'null');
    this.currentView = 'home';
    this.init();
  }

  init() {
    this.bindEvents();
    this.updateUserUI();
    this.navigate('home');
  }

  bindEvents() {
    document.querySelectorAll('.nav-link').forEach(link => {
      link.addEventListener('click', (e) => {
        e.preventDefault();
        const targetView = link.getAttribute('data-view');
        this.navigate(targetView);
      });
    });

    const searchInput = document.getElementById('search-input');
    if (searchInput) {
      searchInput.addEventListener('input', (e) => {
        const query = e.target.value.trim();
        if (query.length >= 2) {
          this.searchMovies(query);
        }
      });
    }

    const filterBtn = document.getElementById('apply-filter-btn');
    if (filterBtn) {
      filterBtn.addEventListener('click', () => this.applyFilters());
    }
  }

  async fetchApi(endpoint, options = {}) {
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers
    };
    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    try {
      const res = await fetch(`${API_BASE_URL}${endpoint}`, { ...options, headers });
      const json = await res.json();
      return json;
    } catch (err) {
      console.error('API Error:', err);
      return { success: false, message: 'Sunucuya bağlanılamadı.' };
    }
  }

  updateUserUI() {
    const authBox = document.getElementById('user-auth-box');
    if (!authBox) return;

    if (this.user) {
      authBox.innerHTML = `
        <span style="color:var(--primary-gold); font-weight:600;">👤 ${this.user.username}</span>
        <button class="btn btn-secondary" id="logout-btn">Çıkış</button>
      `;
      document.getElementById('logout-btn').addEventListener('click', () => this.logout());
    } else {
      authBox.innerHTML = `
        <button class="btn btn-primary" onclick="app.showAuthModal()">Giriş Yap</button>
      `;
    }
  }

  showAuthModal() {
    const modal = document.getElementById('auth-modal');
    if (modal) modal.style.display = 'flex';
  }

  hideAuthModal() {
    const modal = document.getElementById('auth-modal');
    if (modal) modal.style.display = 'none';
  }

  async login(username, password) {
    const res = await this.fetchApi('/auth/login.php', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    });

    if (res.success) {
      this.token = res.data.access_token;
      this.user = res.data.user;
      localStorage.setItem('access_token', this.token);
      localStorage.setItem('user_data', JSON.stringify(this.user));
      this.updateUserUI();
      this.hideAuthModal();
      alert('Giriş başarılı!');
    } else {
      alert(res.message || 'Giriş başarısız.');
    }
  }

  logout() {
    this.token = null;
    this.user = null;
    localStorage.removeItem('access_token');
    localStorage.removeItem('user_data');
    this.updateUserUI();
    this.navigate('home');
  }

  navigate(viewName, params = {}) {
    this.currentView = viewName;
    document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
    const activeLink = document.querySelector(`.nav-link[data-view="${viewName}"]`);
    if (activeLink) activeLink.classList.add('active');

    const views = ['home', 'discover', 'search', 'library', 'diary', 'detail', 'admin'];
    views.forEach(v => {
      const el = document.getElementById(`view-${v}`);
      if (el) el.style.display = (v === viewName) ? 'block' : 'none';
    });

    if (viewName === 'home') this.loadHome();
    if (viewName === 'discover') this.loadDiscover();
    if (viewName === 'library') this.loadLibrary();
    if (viewName === 'diary') this.loadDiary();
    if (viewName === 'detail') this.loadMovieDetail(params.id);
  }

  async loadHome() {
    const popularRes = await this.fetchApi('/movies/popular.php?per_page=10');
    const topRatedRes = await this.fetchApi('/movies/top_rated.php?per_page=10');

    this.renderMovieGrid('popular-grid', popularRes.data || []);
    this.renderMovieGrid('top-rated-grid', topRatedRes.data || []);
  }

  async loadDiscover() {
    const filtersRes = await this.fetchApi('/discovery/filters.php');
    if (filtersRes.success) {
      const genreSelect = document.getElementById('genre-select');
      if (genreSelect && filtersRes.data.genres) {
        genreSelect.innerHTML = '<option value="">Tüm Türler</option>' +
          filtersRes.data.genres.map(g => `<option value="${g.id}">${g.name}</option>`).join('');
      }
    }
    this.applyFilters();
  }

  async applyFilters() {
    const genre = document.getElementById('genre-select')?.value || '';
    const yearMin = document.getElementById('year-min')?.value || '';
    const ratingMin = document.getElementById('rating-min')?.value || '';
    const sortBy = document.getElementById('sort-by')?.value || 'popularity';

    const params = new URLSearchParams({
      ...(genre && { genres: genre }),
      ...(yearMin && { year_min: yearMin }),
      ...(ratingMin && { min_rating: ratingMin }),
      sort_by: sortBy
    });

    const res = await this.fetchApi(`/movies/list.php?${params.toString()}`);
    this.renderMovieGrid('discover-grid', res.data || []);
  }

  async searchMovies(query) {
    const res = await this.fetchApi(`/movies/search.php?q=${encodeURIComponent(query)}`);
    this.renderMovieGrid('search-grid', res.data?.movies || []);
  }

  async loadMovieDetail(id) {
    const res = await this.fetchApi(`/movies/detail.php?id=${id}`);
    if (!res.success || !res.data) return;

    const movie = res.data;
    const detailContainer = document.getElementById('view-detail');
    detailContainer.innerHTML = `
      <div class="detail-banner" style="background-image: url('https://image.tmdb.org/t/p/w1280${movie.backdrop_path}');">
        <div class="detail-overlay">
          <img class="detail-poster" src="https://image.tmdb.org/t/p/w342${movie.poster_path}" alt="${movie.title}">
          <div class="detail-content">
            <h1 class="detail-title">${movie.title} (${movie.release_year || ''})</h1>
            <p style="color:var(--text-muted);">${movie.original_title} • ${movie.runtime || ''} dk</p>
            <div style="margin-top:0.5rem; color:var(--primary-gold); font-size:1.1rem; font-weight:bold;">
              ⭐ TMDB: ${movie.vote_average} | Uygulama: ${movie.app_vote_average || 'N/A'}
            </div>
            <div class="detail-actions">
              <button class="btn btn-primary" onclick="app.actionMovie(${movie.id}, 'favorite')">❤️ Favori</button>
              <button class="btn btn-secondary" onclick="app.actionMovie(${movie.id}, 'watchlist')">🔖 Watchlist</button>
              <button class="btn btn-secondary" onclick="app.actionMovie(${movie.id}, 'watched')">✓ İzledim</button>
            </div>
          </div>
        </div>
      </div>
      <div style="background:var(--bg-surface); padding:1.5rem; border-radius:8px;">
        <h3 style="color:var(--primary-gold); margin-bottom:0.5rem;">Özet</h3>
        <p>${movie.overview || 'Özet bulunmuyor.'}</p>
      </div>
    `;
  }

  async actionMovie(movieId, action) {
    if (!this.user) {
      alert('Bu işlem için giriş yapmalısınız.');
      this.showAuthModal();
      return;
    }
    const res = await this.fetchApi(`/${action}s/add.php`, {
      method: 'POST',
      body: JSON.stringify({ movie_id: movieId })
    });
    alert(res.message || 'İşlem tamamlandı.');
  }

  renderMovieGrid(containerId, movies) {
    const container = document.getElementById(containerId);
    if (!container) return;

    if (movies.length === 0) {
      container.innerHTML = '<p style="color:var(--text-muted);">Film bulunamadı.</p>';
      return;
    }

    container.innerHTML = movies.map(m => `
      <div class="movie-card" onclick="app.navigate('detail', {id: ${m.id}})">
        <img class="movie-poster" src="https://image.tmdb.org/t/p/w342${m.poster_path}" alt="${m.title}" onerror="this.src='https://via.placeholder.com/300x450?text=Poster'">
        <div class="movie-info">
          <div class="movie-title">${m.title}</div>
          <div class="movie-meta">
            <span>${m.release_year || ''}</span>
            <span class="rating-badge">⭐ ${m.vote_average || 0}</span>
          </div>
        </div>
      </div>
    `).join('');
  }
}

let app;
document.addEventListener('DOMContentLoaded', () => {
  app = new VideoShowApp();
});
