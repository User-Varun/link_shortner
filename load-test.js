import http from 'k6/http';
import { check, fail, group } from 'k6';

const BASE = 'http://localhost:8080/api/shortner';
const JSON_HEADERS = { 'Content-Type': 'application/json' };

export const options = {
    // Ramp: 0 → 10 → 50 → 100 → 200 → 0
    scenarios: {
        ramp: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '10s', target: 10 },
                { duration: '20s', target: 50 },
                { duration: '20s', target: 100 },
                { duration: '20s', target: 200 },
                { duration: '10s', target: 0 },
            ],
        },
    },
    thresholds: {
        'http_req_duration{endpoint:redirect}': ['p(95)<200'],
        'http_req_duration{endpoint:shorten}': ['p(95)<500'],
        http_req_failed: ['rate<0.01'],
    },
};

export function setup() {
    // Pre-create 1000 links so redirects have data to hit
    const codes = [];
    for (let i = 0; i < 1000; i++) {
        const res = http.post(
            `${BASE}`,
            JSON.stringify({ url : `https://example.com/preload/${i}` }),
            { headers: JSON_HEADERS }
        );
        if (res.status === 200) {
            const body = res.json();
            if (body.shortCode) {
                codes.push(body.shortCode);
            }
        }
    }

    if (codes.length === 0) {
        fail('Setup did not create any short links; check that the API is running.');
    }

    return { codes };
}

export default function (data) {
    // 85% reads (redirects), 15% writes (shorten) — realistic ratio
    if (Math.random() < 0.85) {
        group('redirect (read)', () => {
            const code = data.codes[Math.floor(Math.random() * data.codes.length)];
            const res = http.get(`${BASE}/${code}`, {
                tags: { endpoint: 'redirect' },
                redirects: 0,
            });
            check(res, {
                'status is 302': (r) => r.status === 302,
                'location header set': (r) => r.headers['Location'] !== undefined,
            });
        });
    } else {
        group('shorten (write)', () => {
            const uniqueUrl = `https://example.com/page/${__VU}-${__ITER}-${Date.now()}`;
            const res = http.post(
                `${BASE}`,
                JSON.stringify({ url : uniqueUrl }),
                { headers: JSON_HEADERS, tags: { endpoint: 'shorten' } }
            );
            check(res, {
                'status is 200': (r) => r.status === 200,
            });
        });
    }
}