var __values = (this && this.__values) || function (o) {
  var s = typeof Symbol === "function" && Symbol.iterator,
    m = s && o[s],
    i = 0;
  if (m) return m.call(o);
  if (o && typeof o.length === "number") return {
    next: function () {
      if (o && i >= o.length) o = void 0;
      return {
        value: o && o[i++],
        done: !o
      };
    }
  };
  throw new TypeError(s ? "Object is not iterable." : "Symbol.iterator is not defined.");
};

export function pointInRect(pt, rect) {
  if (!rect) {
    return;
  }
  if (rect.ex == null) {
    calcRightBottom(rect);
  }
  if (!rect.rotate ||
    // rect.width < 20 ||
    // rect.height < 20 ||
    rect.rotate % 360 === 0) {
    return pt.x > rect.x && pt.x < rect.ex && pt.y > rect.y && pt.y < rect.ey;
  }
  if (!rect.center) {
    calcCenter(rect);
  }
  var pts = [{
      x: rect.x,
      y: rect.y
    },
    {
      x: rect.ex,
      y: rect.y
    },
    {
      x: rect.ex,
      y: rect.ey
    },
    {
      x: rect.x,
      y: rect.ey
    },
  ];
  pts.forEach(function (item) {
    rotatePoint(item, rect.rotate, rect.center);
  });
  return pointInVertices(pt, pts);
}

export function rotatePoint(pt, angle, center) {
  if (!angle || angle % 360 === 0) {
    return;
  }
  var a = (angle * Math.PI) / 180;
  var x = (pt.x - center.x) * Math.cos(a) -
    (pt.y - center.y) * Math.sin(a) +
    center.x;
  var y = (pt.x - center.x) * Math.sin(a) +
    (pt.y - center.y) * Math.cos(a) +
    center.y;
  pt.x = x;
  pt.y = y;
  pt.prev && rotatePoint(pt.prev, angle, center);
  pt.next && rotatePoint(pt.next, angle, center);
}

export function calcRightBottom(rect) {
  rect.ex = rect.x + rect.width;
  rect.ey = rect.y + rect.height;
}

export function calcCenter(rect) {
  if (!rect.center) {
    rect.center = {};
  }
  rect.center.x = rect.x + rect.width / 2;
  rect.center.y = rect.y + rect.height / 2;
}

export function pointInVertices(point, vertices) {
  var e_1, _a;
  if (vertices.length < 3) {
    return false;
  }
  var isIn = false;
  var last = vertices[vertices.length - 1];
  try {
    for (var vertices_1 = __values(vertices), vertices_1_1 = vertices_1.next(); !vertices_1_1.done; vertices_1_1 = vertices_1.next()) {
      var item = vertices_1_1.value;
      if (last.y > point.y !== item.y > point.y) {
        if (item.x + ((point.y - item.y) * (last.x - item.x)) / (last.y - item.y) >
          point.x) {
          isIn = !isIn;
        }
      }
      last = item;
    }
  } catch (e_1_1) {
    e_1 = {
      error: e_1_1
    };
  } finally {
    try {
      if (vertices_1_1 && !vertices_1_1.done && (_a = vertices_1.return)) _a.call(vertices_1);
    } finally {
      if (e_1) throw e_1.error;
    }
  }
  return isIn;
}
