import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";
import { Snake } from "./Snake";

export class GameMap extends AcGameObject {
    constructor(ctx, parent) {
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;

        this.rows = 13;
        this.cols = 13;

        this.inner_walls_count = 20;
        this.walls = [];

        this.snakes = [
            new Snake({
                id: 0,
                color: "#4876EC",
                r: this.rows - 2,
                c: 1
            }, this),
            new Snake({
                id: 1,
                color: "#FFC107",
                r: 1, 
                c: this.cols - 2
            }, this),
        ];

        
    }

    check_connectivity(g, sx, sy, tx, ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = true;

        let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
        for (let i = 0; i < 4; i++) {
            let x = sx + dx[i], y = sy + dy[i];
            if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty))
                return true;
        }
        return false;
    }

    create_walls() {
        const g = [];
        for (let r = 0; r < this.rows; r++) {
            g[r] = [];
            for (let c = 0; c < this.cols; c++) {
                g[r][c] = false;
            }
        }

        for (let r = 0; r < this.rows; r++)
            g[r][0] = g[r][this.cols - 1] = true;

        for (let c = 0; c < this.cols; c++)
            g[0][c] = g[this.rows - 1][c] = true;

        // create random walls
        for (let i = 0; i < this.inner_walls_count / 2; i++) {
            for (let j = 0; j < 1000; j++) {
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                if (g[r][c] || g[this.rows - 1 - r][this.cols - 1 - c]) continue;
                if (r === this.rows - 2 && c == 1 || (r == 1 && c === this.cols - 2))
                    continue;

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;
                break;
            }
        }

        const copy_g = JSON.parse(JSON.stringify(g));
        if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2)) return false;

        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c])
                    this.walls.push(new Wall(r, c, this));
            }
        }
        return true;
    }

    add_listening_events() {
        this.ctx.canvas.focus();

        const [snake0, snake1] = this.snakes;
        this.ctx.canvas.addEventListener('keydown', e => {
            switch (e.key) {
                case 'w': snake0.set_direction(0); break;
                case 'd': snake0.set_direction(1); break;
                case 's': snake0.set_direction(2); break;
                case 'a': snake0.set_direction(3); break;
                case 'ArrowUp': snake1.set_direction(0); break;
                case 'ArrowRight': snake1.set_direction(1); break;
                case 'ArrowDown': snake1.set_direction(2); break;
                case 'ArrowLeft': snake1.set_direction(3); break;
            }
        });
    }

    start() {
        for (let i = 0; i < 1000; ++i)
            if (this.create_walls()) break;

        this.add_listening_events();
    }

    check_valid(cell) {
        for (const wall of this.walls)
            if (wall.r === cell.r && wall.c === cell.c)
                return false;
        
        for (const snake of this.snakes) {
            let k = snake.cells.length;
            if (!snake.check_tail_increasing())
                k--;
            for (let i = 0; i < k; ++i)
                if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                    return false;
        }
        return true;
    }

    check_ready() {
        for (const snake of this.snakes)
            if (snake.direction === -1 || snake.status !== "idle") return false;
        return true;
    }

    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    next_step() {
        for (const snake of this.snakes)
            snake.next_step();
    }

    update() {
        this.update_size();
        if (this.check_ready())
            this.next_step();
        this.render();
    }

    render() {
        const color_even = "#AAD751", color_odd = "#A2D149";
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if ((r + c) % 2 === 0)
                    this.ctx.fillStyle = color_even;
                else
                    this.ctx.fillStyle = color_odd;
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}
