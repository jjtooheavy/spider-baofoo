!
    function() {
        function s() {
            var t = "CsD5z3NijK9maUv6".split("");
            this.d = function(e) {
                if (null == e || void 0 == e) return e;
                if (0 != e.length % 2) throw Error("1100");
                for (var r = [], i = 0; i < e.length; i++) {
                    0 == i % 2 && r.push("%");
                    for (var s = t,
                             n = 0; n < s.length; n++) if (e.charAt(i) == s[n]) {
                        r.push(n.toString(16));
                        break
                    }
                }
                return decodeURIComponent(r.join(""))
            }
        }
        var t = (new s).d,
            n = (new s).d,
            e = (new s).d,
            i = (new s).d,
            r = (new s).d; !
            function() {
                function R(e) {
                    if (null == e) return null;
                    for (var i = [], t = 0, r = e.length; r > t; t++) {
                        var n = e[t];
                        i[t] = at[16 * (n >>> 4 & 15) + (15 & n)]
                    }
                    return i
                }
                function D(t) {
                    var n = [];
                    if (null == t || void 0 == t || 0 == t.length) return X();
                    if (64 <= t.length) {
                        n = [];
                        if (null != t && 0 != t.length) {
                            if (64 > t.length) throw Error(i("5s5C5C55"));
                            for (var e = 0; 64 > e; e++) n[e] = t[0 + e]
                        }
                        return n
                    }
                    for (e = 0; 64 > e; e++) n[e] = t[e % t.length];
                    return n
                }
                function H(t) {
                    var e = 4294967295;
                    if (null != t) for (var n = 0; n < t.length; n++) e = e >>> 8 ^ Y[255 & (e ^ t[n])];
                    t = q(4294967295 ^ e);
                    e = t.length;
                    if (null == t || 0 > e) t = new String(i(""));
                    else {
                        for (var n = [], s = 0; e > s; s++) n.push(lt(t[s]));
                        t = n.join(r(""))
                    }
                    return t
                }
                function G(o, u, a) {
                    var c, _ = [r("zv"), n("ii"), e("5i"), r("5z"), e("zm"), t("5N"), n("zi"), e("3D"), t("53"), t("3i"), e("z9"), t("iz"), n("5s"), e("3N"), e("39"), n("35"), t("55"), e("5j"), r("z6"), t("i3"), r("3z"), t("Na"), r("i5"), i("Ni"), e("zK"), n("Nm"), i("N3"), e("N5"), n("Dm"), n("zU"), e("zs"), n("5K"), i("NK"), i("Nv"), e("zD"), e("3a"), r("3s"), i("zz"), i("33"), n("iC"), e("z3"), t("is"), n("ND"), t("N9"), n("iK"), i("3C"), e("3j"), e("Nz"), t("Nj"), e("5D"), i("3K"), t("ij"), n("N6"), r("5C"), r("za"), e("iD"), n("D6"), n("Ns"), i("zj"), t("NU"), n("zN"), i("NN"), r("i9"), t("iN")],
                        h = t("z5"),
                        s = [];
                    if (1 == a) a = o[u],
                        c = 0,
                        s.push(_[a >>> 2 & 63]),
                        s.push(_[(a << 4 & 48) + (c >>> 4 & 15)]),
                        s.push(h),
                        s.push(h);
                    else if (2 == a) a = o[u],
                        c = o[u + 1],
                        o = 0,
                        s.push(_[a >>> 2 & 63]),
                        s.push(_[(a << 4 & 48) + (c >>> 4 & 15)]),
                        s.push(_[(c << 2 & 60) + (o >>> 6 & 3)]),
                        s.push(h);
                    else if (3 == a) a = o[u],
                        c = o[u + 1],
                        o = o[u + 2],
                        s.push(_[a >>> 2 & 63]),
                        s.push(_[(a << 4 & 48) + (c >>> 4 & 15)]),
                        s.push(_[(c << 2 & 60) + (o >>> 6 & 3)]),
                        s.push(_[63 & o]);
                    else throw Error(i("5s5C5s5C"));
                    return s.join(e(""))
                }
                function X() {
                    for (var e = [], t = 0; 64 > t; t++) e[t] = 0;
                    return e
                }
                function v(t, n, a, s) {
                    if (null != t && 0 != t.length) {
                        if (null == n) throw Error(r("5s5C5C5z"));
                        if (t.length < s) throw Error(i("5s5C5C55"));
                        for (var e = 0; s > e; e++) n[a + e] = t[0 + e]
                    }
                }
                function q(e) {
                    var t = [];
                    t[0] = e >>> 24 & 255;
                    t[1] = e >>> 16 & 255;
                    t[2] = e >>> 8 & 255;
                    t[3] = 255 & e;
                    return t
                }
                function I(t) {
                    if (null == t || void 0 == t) return t;
                    t = encodeURIComponent(t);
                    for (var s = [], a = t.length, r = 0; a > r; r++) if (t.charAt(r) == e("D3")) if (a > r + 2) s.push(ft(t.charAt(++r) + n("") + t.charAt(++r))[0]);
                    else throw Error(i("5s5C5C5K"));
                    else s.push(t.charCodeAt(r));
                    return s
                }
                function ft(t) {
                    if (null == t || 0 == t.length) return [];
                    t = new String(t);
                    for (var i = [], r = t.length / 2, n = 0, e = 0; r > e; e++) {
                        var s = parseInt(t.charAt(n++), 16) << 4,
                            a = parseInt(t.charAt(n++), 16);
                        i[e] = u(s + a)
                    }
                    return i
                }
                function lt(e) {
                    var t = [];
                    t.push(Q[e >>> 4 & 15]);
                    t.push(Q[15 & e]);
                    return t.join(r(""))
                }
                function x(t, i) {
                    if (null == t || null == i || t.length != i.length) return t;
                    for (var n = [], e = 0, r = t.length; r > e; e++) n[e] = K(t[e], i[e]);
                    return n
                }
                function K(t, e) {
                    t = u(t);
                    e = u(e);
                    return u(t ^ e)
                }
                function ht(t, e) {
                    return u(t + e)
                }
                function u(t) {
                    if ( - 128 > t) return u(128 - ( - 128 - t));
                    if (t >= -128 && 127 >= t) return t;
                    if (t > 127) return u( - 129 + t - 127);
                    throw Error(n("5s5C5C5s"))
                }
                function ut(a) {
                    function p() {
                        for (var a = [i("zsNDNsNzNKDCzU3zDCz5N6NvNzN3Nvi5N3NzDCzaNKNiNjiz"), i("zsNzN6NDN3DCzNNsNvNii5N6NvNiDC35izNz"), e("zsNzN6NDN3DCzjN3NDiDN3ii"), i("zsNzN6NDN3DCzUNKNvNiDC35izNz"), i("zsNiN3NvN5iKDCzNzD"), e("zsiDNsND"), e("zsiDNsNDNKN5DC3ziKiCN3i5N3izizNKNvNi"), r("zsiDNKNsNaDCzDNaNsN5Nm"), r("zDNsizNsNvNi"), n("zDNsi3NjNsi3i5DC5K55"), e("zDN3NaNaDCzU3z"), n("zDNKizi5iziDN3NsNUDC3NN3iDNsDC35N3iDNKNN"), n("zDN6NzN6NvNKDCzU3z"), t("zDN6N6NmNUNsNvDCz6NaNzDC35iziKNaN3"), i("zDiDNsNiNiNsNzN6N5NKN6"), i("zDiDN6NsNziiNsiK"), i("z5NsNaNKNDiDNK"), r("z5NsNaNKNNN6iDNvNKNsNvDCzNzD"), t("z5Nsi5izN3NaNaNsiD"), n("z5Nsi5i3NsNa"), e("z5N3NvizNsi3iD"), r("z5N3Nvizi3iDiKDCziN6izNjNKN5"), e("z5NjNsNaNmNzi3i5izN3iD"), i("z5N6NaN6NvNvNsDCzU3z"), i("z5N6iCiCN3iDiCNaNsizN3DCziN6izNjNKN5DCzaNKNiNjiz"), i("zzN3N9Ns3Ni3DCzaziz5DC35NsNvi5DCzUN6NvN6"), r("zzN3i5NzN3NUN6NvNs"), i("zzzNzmNsNKDU35zD"), t("zzN6izi3NU"), t("z3NvNiiDNsiNN3iDi5DCzU3z"), e("z3iDNsi5DCzDN6NaNzDCzK3zz5"), t("z3i3iDN6i5izNKNaN3"), e("zNNsNvNi35N6NvNi"), t("zNN6iDizN3"), n("zNiDNsNvNmNaNKNvDCziN6izNjNKN5DCzjN3NsiNiK"), n("zNiDN3NvN5NjDC35N5iDNKiCizDCzU3z"), t("ziNsNDiDNKN6NaNs"), r("ziNKNiNK"), t("ziNKi5NjNs"), n("ziN6i3NziKDCz6NaNzDC35iziKNaN3"), e("zii3NaNKNU"), e("zii3NvNi35N3N6"), r("zjNsN3izizN3Nvi5N5NjiiN3NKNaN3iD"), i("zjNsiDiDNKNvNiizN6Nv"), i("zjNKiDNsNiNKNvN6DC35NsNvi5DCzizD"), r("zKNUiCNsN5iz"), t("zKNvNNN6iDNUNsNaDC3DN6NUNsNv"), i("zmNsN5i5izz6NvN3"), e("zmNKNvN6DCzU3z"), r("zmN6i9i3NmNsDCziN6izNjNKN5DC3CiD5Nzv"), t("zaN6NjNKizDCzii3N9NsiDNsizNK"), i("zaN6NUNs"), i("zai3N5NKNzNsDCzDiDNKNiNjiz"), e("zai3N5NKNzNsDCzNNsij"), i("zUNsNiNvN3izN6"), n("zUNsNaNii3NvDCziN6izNjNKN5"), n("zUNsizi3iDNsDCzU3zDC35N5iDNKiCizDCz5NsiCNKizNsNai5"), t("zUN3NvNaN6"), r("zUNKNvNizaNK33DUz3ijizzD"), n("zUN6N6NazDN6iDNsNv"), i("zU35DC3CzUNKNvN5NjN6"), t("zU35DC3DN3NNN3iDN3NvN5N3DC35NsNvi5DC35N3iDNKNN"), n("zvN3iii5DCziN6izNjNKN5DCzU3z"), e("zvNKNsNiNsiDNsDC35N6NaNKNz"), t("zviKNsNaNs"), r("3CNsNaNsN5N3DC35N5iDNKiCizDCzU3z"), e("3CNsiCiKiDi3i5"), e("3CN3iDiCN3izi3Ns"), t("3CNaNsiKNDNKNaNa"), e("3CzUNKNvNizaNK33"), r("3DNsN5NjNsNvNs"), e("3DN6N5NmiiN3NaNa"), e("35NsiiNsi5NzN3N3"), n("35N5iDNKiCizDCzU3zDCzDN6NaNz"), n("35N3NiN6N3DC3CiDNKNviz"), e("35NjN6iiN5NsiDNzDCziN6izNjNKN5"), n("35NKNUzjN3NK"), t("35NvNsiCDCzK3zz5"), r("3zNaiiNizUN6NvN6"), i("3ziiDCz5N3NvDCzU3zDCz5N6NvNzN3Nvi5N3NzDCz3ijiziDNsDCzDN6NaNz"), t("33NDi3Nvizi3"), r("33NUiCi3i5Nj"), t("33NvNKiNN3iDi5"), e("33izN6iCNKNs"), r("3NNaNsNzNKNUNKiDDC35N5iDNKiCiz"), r("3iNKNzN3DCzaNsizNKNv"), e("vzmmm6v39vjm"), e("v3jUjvvNKNjivzmj9Uv39vjm"), i("v3jUjvvNKNjivzmmm6v39vjm"), i("v3jUjvvNKNjiv39vjmvzmUK5"), t("v3jUjvvNKNjiv3mU9Kvzm9Ks"), t("v3jUjvvNKNjivNKNmCvK9Uj6"), i("v3jUjvvNKNjivN93mivzmUK5"), n("v3jUjvvNKNjiviKC93vij6jC"), e("v3jUjvvNKNjivimmjNvKmmKs"), t("v3jUjvvNKNjivj9sjavN93mi"), i("v3jUjvvNKNjivKK9mNvzmK9N"), i("v39vjmvzmUK5"), r("v3mKmav3KajN"), i("v3mv9vvjmU96vKKmj3vKmmKs"), e("vNKNmCv39vjmvzmUK5"), t("vNKNmKvN9U95v39iK9vzmUK5"), e("vNKNmKvN9U95vjjjKDvzmUK5"), n("vN93mivzmUK5"), e("vKK9mNvzmK9N"), r("vKmmKsvzmUK5"), n("vNKNmCvimmjNvNKjjvvzmUK5"), i("vimmjNvNKjjvvzmUK5"), i("vN9CjivN93mivzmUK5"), n("vzmmm6v39vjm36zizD5D555s5D"), i("vN93mivzmUK536zizD5D555s5D"), n("v3mv9vvjmU96vN9U95vKmmKsvzmUK5"), r("v3jUjvvNKNjivKmmKsvzmUK5"), e("vzmjmUvKmmKsDC3CiDN6"), i("vzmjmUv39vjmDC3CiDN6"), t("vjjmmKvNKvKavzmjmUvzmj9UvKmmKs"), r("vjjmmKvNKvKavzmjmUvimmjNv39vjm")], o = [], s = 0; s < a.length; s++) try {
                            var _ = a[s];
                            N()(_) && o.push(_)
                        } catch(c) {
                            i("NNN6NvizDCNzN3izN3N5izDCN3iDiDN6iD")
                        }
                        return o.join(t("5m"))
                    }
                    function N() {
                        function c(e) {
                            var t = {};
                            return i.style.fontFamily = e,
                                u.appendChild(i),
                                t.height = i.offsetHeight,
                                t.width = i.offsetWidth,
                                u.removeChild(i),
                                t
                        }
                        var a = [n("NUN6NvN6i5iCNsN5N3"), r("i5NsNvi5DUi5N3iDNKNN"), r("i5N3iDNKNN")],
                            o = [],
                            s = n("iiiiiiNUNUNUNUNUNUNUNUNUNUNaNaNK"),
                            h = t("5i5DiCij"),
                            u = _.body,
                            i = _.createElement(e("i5iCNsNv"));
                        i.style.fontSize = h;
                        i.style.visibility = r("NjNKNzNzN3Nv");
                        i.innerHTML = s;
                        for (s = 0; s < a.length; s++) o[s] = c(a[s]);
                        return function(r) {
                            for (var t = 0; t < o.length; t++) {
                                var i = c(r + e("Da") + a[t]),
                                    n = o[t];
                                if (i.height !== n.height || i.width !== n.width) return ! 0
                            }
                            return ! 1
                        }
                    }
                    function v() {
                        var r = null,
                            s = null,
                            a = [];
                        try {
                            s = _.createElement(t("N5NsNviNNsi5")),
                                r = s[i("NiN3izz5N6NvizN3ijiz")](n("iiN3NDNiNa")) || s[n("NiN3izz5N6NvizN3ijiz")](e("N3ijiCN3iDNKNUN3NvizNsNaDUiiN3NDNiNa"))
                        } catch(o) {}
                        if (!r) return a;
                        try {
                            a.push(r.getSupportedExtensions())
                        } catch(c) {}
                        try {
                            a.push(y(r, s))
                        } catch(u) {}
                        return a.join(t("5m"))
                    }
                    function y(t, _) {
                        try {
                            var c = e("NsiziziDNKNDi3izN3DCiNN3N55DDCNsiziziD3NN3iDizN3ij5mDCiNNsiDiKNKNvNiDCiNN3N55DDCiNNsiDiKNKNv3zN3ijz5N6N6iDNzNKNvNsizN35mDCi3NvNKNNN6iDNUDCiNN3N55DDCi3NvNKNNN6iDNUz6NNNNi5N3iz5mDCiNN6NKNzDCNUNsNKNvDjDKDCimDCDCDCiNNsiDiKNKNv3zN3ijz5N6N6iDNzNKNvNsizN3DC5UDCNsiziziD3NN3iDizN3ijDCDmDCi3NvNKNNN6iDNUz6NNNNi5N3iz5mDCDCDCNiNa363CN6i5NKizNKN6NvDC5UDCiNN3N55zDjNsiziziD3NN3iDizN3ijDaDC5CDaDC5sDK5mDCiU"),
                                u = r("iCiDN3N5NKi5NKN6NvDCNUN3NzNKi3NUiCDCNNNaN6Nsiz5mDCiNNsiDiKNKNvNiDCiNN3N55DDCiNNsiDiKNKNv3zN3ijz5N6N6iDNzNKNvNsizN35mDCiNN6NKNzDCNUNsNKNvDjDKDCimDCDCDCNiNa36zNiDNsNiz5N6NaN6iDDC5UDCiNN3N55zDjiNNsiDiKNKNv3zN3ijz5N6N6iDNzNKNvNsizN3DaDC5CDaDC5sDK5mDCiU"),
                                s = t.createBuffer();
                            t.bindBuffer(t.ARRAY_BUFFER, s);
                            var h = new Float32Array([ - .2, -.9, 0, .4, -.26, 0, 0, .732134444, 0]);
                            t.bufferData(t.ARRAY_BUFFER, h, t.STATIC_DRAW);
                            s.k = 3;
                            s.l = 3;
                            var n = t.createProgram(),
                                a = t.createShader(t.VERTEX_SHADER);
                            t.shaderSource(a, c);
                            t.compileShader(a);
                            var o = t.createShader(t.FRAGMENT_SHADER);
                            return t.shaderSource(o, u),
                                t.compileShader(o),
                                t.attachShader(n, a),
                                t.attachShader(n, o),
                                t.linkProgram(n),
                                t.useProgram(n),
                                n.n = t.getAttribLocation(n, i("NsiziziD3NN3iDizN3ij")),
                                n.m = t.getUniformLocation(n, i("i3NvNKNNN6iDNUz6NNNNi5N3iz")),
                                t.enableVertexAttribArray(n.o),
                                t.vertexAttribPointer(n.n, s.k, t.FLOAT, !1, 0, 0),
                                t.uniform2f(n.m, 1, 1),
                                t.drawArrays(t.TRIANGLE_STRIP, 0, s.l),
                                m(_[r("izN6zzNsizNs333Dza")]())
                        } catch(l) {
                            return i("iiN3NDNiNaDCN3ijN5N3iCizNKN6Nv")
                        }
                    }
                    function E() {
                        var a = _.createElement(e("NzNKiN")),
                            s = [],
                            c = [r("zsN5izNKiNN3zDN6iDNzN3iD"), e("zsN5izNKiNN3z5NsiCizNKN6Nv"), n("zsiCiC3iN6iDNmi5iCNsN5N3"), t("zDNsN5NmNiiDN6i3NvNz"), n("zDi3izizN6NvzNNsN5N3"), n("zDi3izizN6NvzjNKNiNjNaNKNiNjiz"), t("zDi3izizN6Nv35NjNsNzN6ii"), n("zDi3izizN6Nv3zN3ijiz"), i("z5NsiCizNKN6Nv3zN3ijiz"), n("ziiDNsiK3zN3ijiz"), t("zjNKNiNjNaNKNiNjiz"), r("zjNKNiNjNaNKNiNjiz3zN3ijiz"), r("zKNvNsN5izNKiNN3zDN6iDNzN3iD"), t("zKNvNsN5izNKiNN3z5NsiCizNKN6Nv"), i("zKNvNsN5izNKiNN3z5NsiCizNKN6Nv3zN3ijiz"), r("zKNvNNN6zDNsN5NmNiiDN6i3NvNz"), r("zKNvNNN63zN3ijiz"), t("zUN3Nvi3"), t("zUN3Nvi33zN3ijiz"), n("35N5iDN6NaNaNDNsiD"), t("3zNjiDN3N3zzzzNsiDNm35NjNsNzN6ii"), r("3zNjiDN3N3zzzNNsN5N3"), e("3zNjiDN3N3zzzjNKNiNjNaNKNiNjiz"), n("3zNjiDN3N3zzzaNKNiNjiz35NjNsNzN6ii"), t("3zNjiDN3N3zz35NjNsNzN6ii"), r("3iNKNvNzN6ii"), e("3iNKNvNzN6iizNiDNsNUN3"), t("3iNKNvNzN6ii3zN3ijiz")];
                        if (!window[e("NiN3izz5N6NUiCi3izN3Nz35iziKNaN3")]) return s.join(t(""));
                        for (var o = 0; o < c.length; o++) try {
                            _.body.appendChild(a),
                                a.style.color = c[o],
                                s.push(c[o]),
                                s.push(window[i("NiN3izz5N6NUiCi3izN3Nz35iziKNaN3")](a).getPropertyValue(i("N5N6NaN6iD"))),
                                _.body.removeChild(a)
                        } catch(u) {
                            s.push(r("NiN3izDCi5iKi5izN3NUDCN5N6NaN6iDi5DCN3ijN5N3iCizNKN6Nv"))
                        }
                        return s.join(i("59"))
                    }
                    function f() {
                        try {
                            var a = _.createElement(i("N5NsNviNNsi5")),
                                s = a[r("NiN3izz5N6NvizN3ijiz")](n("5DNz")),
                                o = t("NUiiz5DCNvNmNDNsNNN9N6iDNzDCiCNji5NiNaiKDCN3ijiNizDCi9isNKi3DaDCvsmU9CDCiziCNji5izD659D6i3NjNDNiizNKN5DvNUN6D6NaN3iNiNNs");
                            s.textBaseline = t("izN6iC");
                            s.font = n("5i5CiCijDCDizsiDNKNsNaDi");
                            s.textBaseline = i("NsNaiCNjNsNDN3izNKN5");
                            s.fillStyle = i("D5NN5N5C");
                            s.fillRect(125, 1, 62, 20);
                            s.fillStyle = t("D55C5N5K");
                            s.fillText(o, 2, 15);
                            s.fillStyle = n("iDNiNDNsDj5s5C5DDaDC5D5C5zDaDC5CDaDC5CDv5iDK");
                            s.fillText(o, 4, 17);
                            return a[e("izN6zzNsizNs333Dza")]()
                        } catch(c) {
                            return i("N5NsNviNNsi5DCNsiCNKDCN3ijN5N3iCizNKN6Nv")
                        }
                    }
                    function d() {
                        try {
                            return window[i("zsN5izNKiNN33jz6NDN9N3N5iz")] && s.h ? l() : C()
                        } catch(t) {
                            return n("NiN3izDCiCNai3NiNKNvDCi5iziDNKNvNiDCN3ijN5N3iCizNKN6Nv")
                        }
                    }
                    function C() {
                        if (!h[e("iCNai3NiNKNvi5")]) return n("");
                        var _ = [r("5zNiNsNUN3"), r("zsNzNDNaN6N5Nm3CNai3NiNKNv"), e("zsNzN6NDN3z3ijzUNsNvz5z5zzN3izN3N5iz"), t("zsNzN6NDN3z3ijzUNsNvzzN3izN3N5iz"), t("zsNaNsiiNsiDDCzv3Czs3CzKDCi3izNKNai5"), e("zsNaNKN3NzNKizDC3CNai3NiDUzKNv"), t("zsNaNKiCNsiKDC35N3N5i3iDNKiziKDCz5N6NviziDN6NaDC55"), r("zsNaNK3535z6zaN6NiNKNvDCiCNai3NiNKNv"), i("zsNUNsi9N6NvzU3C55zzN6iiNvNaN6NsNzN3iD3CNai3NiNKNv"), i("zsz6zaDCzUN3NzNKNsDC3CNaNsiKNDNsN5NmDC3CNai3NiNKNv"), i("zsiCiC33iC"), t("zsiDN5NjNKz5zszz"), i("zs3NziDC35NKizN335NsNNN3iziKDCiCNai3NiNKNv"), n("zDNsNDiKNaN6NvDC3zN6N6NazDNsiD"), i("zDNsizizNaN3NaN6NiDCziNsNUN3DCzaNsi3NvN5NjN3iD"), e("zDNKizz5N6NUN3izzsNiN3Nviz"), e("zDNKizNzN3NNN3NvNzN3iDDC3si3NKN5Nm35N5NsNv"), n("zDNai3N335izNsN5Nmi5DCzKNvi5izNsNaNaDCzzN3izN3N5izN6iD"), t("z5NsizNsNaNKNvNsziiDN6i3iCDC33iCNzNsizN3"), t("z5NKiziDNKijDCzKz5zsDCz5NaNKN3Nviz"), n("z5NKiziDNKijDCN6NvNaNKNvN3DCiCNai3NiDUNKNv"), r("z5NKiziDNKijDC3DN3N5N3NKiNN3iDDC3CNai3NiDUNKNv"), r("z5N6N6iiN6NvDC33iCNzNsizN3"), t("zzN3NsNa3CNaiKzaNKiNN3DC33iCNzNsizN3"), t("zzN3NNNsi3NaizDCzDiDN6iii5N3iDDCzjN3NaiCN3iD"), r("zzNKiN3jDCzDiDN6iii5N3iDDC3CNai3NiDUzKNv"), n("zzNKiN3jDC3CNai3i5DC3iN3NDDC3CNaNsiKN3iD"), i("zzNKiN3jDC3Nz6zzDCzjN3NaiCN3iDDC3CNai3NiDUNKNv"), n("NzN6i3NDNaN33ziiNKi5izDC3iN3NDDC3CNai3NiNKNv"), t("zzN6iiNvNaN6NsNzN3iDi5DCiCNai3NiNKNv"), n("NzN6iiNvNaN6NsNz33iCNzNsizN3iD"), e("N3zUi3i5NKN53CNai3NiNKNvDCzzzazU5N"), r("z335zvDCzaNsi3NvN5NjDCzUN6i9NKNaNaNsDC3CNai3NiNKNv"), n("z335zvDC35N6NvNsiDDCzs3CzK"), e("z3ijNKNNDCz3iNN3iDiKiiNjN3iDN3"), r("zNNsN5N3NDN6N6NmDC3CNai3NiNKNv"), r("zNNKNaN3DCzzN6iiNvNaN6NsNzN3iDDC3CNai3NiDUNKNv"), i("zNNKNaN3zaNsNDDCiCNai3NiNKNv"), n("zNNaiKz6iDzzNKN3DCziNsNUN3i5DC3CNai3NiNKNv"), t("zNN6NaijDC55DCzDiDN6iii5N3iDDC3CNai3NiNKNv"), t("zN3339z335NjNsiDN3"), r("zizzzaDCz6NDN9N3N5izDC3iN3NDDC3CNai3NiDUNKNvDC5s5NDv5C5C"), r("zizNzsz5z3DC3CNai3NiNKNv"), r("ziNKNvNiN3iD"), i("ziNvN6NUN3DC35NjN3NaNaDCzKNvizN3NiiDNsizNKN6Nv"), t("ziN6N6NiNaN3DCz3NsiDizNjDC3CNai3NiNKNv"), r("ziN6N6NiNaN3DCz3NsiDizNjDC3CNai3NiDUNKNv"), n("ziN6N6NiNaN3DCziN3NsiDi5DC5CDv53Dv5555Dv5C"), r("ziN6N6NiNaN3DC3zNsNaNmDCz3NNNNN3N5izi5DC3CNai3NiNKNv"), n("ziN6N6NiNaN3DC33iCNzNsizN3"), t("zjNsiDNUN6NviKDCzNNKiDN3NNN6ijDC3CNai3NiNKNv"), e("zjNsiDNUN6NviKDC3CNai3NiDUzKNv"), e("zjN3iDN6N3i5DCDNDCziN3NvN3iDNsNai5DCNaNKiNN3"), n("zj3CzzN3izN3N5iz"), n("zjizNUNa53DCNaN6N5NsizNKN6NvDCiCiDN6iNNKNzN3iD"), n("zKz3DC3zNsNDDCiCNai3NiNKNv"), n("NKziN3izizN3iD35N5iDNKiCizNsNDNaN33CNai3NiNKNv"), t("NKzUN3i5NjDCiCNai3NiNKNv"), n("zmNsi5iCN3iDi5NmiKDC3CNsi5i5iiN6iDNzDCzUNsNvNsNiN3iD"), i("zaNsi5iz3CNsi5i5"), t("zaN6NizUN3zKNvDC3CNai3NiNKNvDC5sDv5CDv5CDv5K5553"), r("zaN6NizUN3zKNvDC3CNai3NiNKNvDC5sDv5CDv5CDv5K5N5s"), n("zUNsDUz5N6NvNNNKNiDvN5N6NUDCiCNai3NiNKNv"), i("zUNKN5iDN6i5N6NNizDCz6NNNNNKN5N3DC5D5C5s55"), e("zUNKNvNKNDNsiD3CNai3NiNKNv"), n("zvNsizNKiNN3DCz5NaNKN3Nviz"), t("zvNKiziDN6DC3CzzzNDC3CNai3NiDUzKNv"), n("zvN6NmNKNsDC35i3NKizN3DCz3NvNsNDNaN3iDDC3CNai3NiNKNv"), i("zvN6iDizN6NvDCzKNzN3NvizNKiziKDC35NsNNN3"), t("NviCzs3CzKDC3CNai3NiNKNv"), n("zv3CzaNsi5iz3CNsi5i5"), t("zv3C3CNaNsiKN3iD35NjN3NaNa"), t("NviC3zN6NvNiNDi3zsNzNzNKNv"), t("zviKijzaNsi3NvN5NjN3iD"), e("z6N5izN6i5NjNsiCN3DC35iziDN3NsNUNKNvNiDC35N3iDiNNKN5N3i5"), i("z6NvNaNKNvN3DC35izN6iDNsNiN3DCiCNai3NiDUNKNv"), n("z6iDNDNKizDCzzN6iiNvNaN6NsNzN3iD"), i("3CNsNvNzN6DC3iN3NDDC3CNai3NiNKNv"), e("3CNsiDN6NUDv3z3NDCiCNaNsiKN3iDDCiCNai3NiNKNv"), r("3CzzzNDCNKNvizN3NiiDNsNzN6DCNzN6DC3iN3NDzmNKiz"), i("3CzzzNDU3jz5NjNsNvNiN3DC3NNKN3iiN3iD"), i("3CNjN6izN6z5N3NvizN3iD3CNai3NiNKNv5sDv5sDv5DDv5D"), r("3CNKN5Nsi5Ns"), n("3CNaNsiKz6NvDC3CNai3NiDUNKNv"), t("3s3s5D5C5s55DCzNNKiDN3NNN6ijDC3CNai3NiNKNv"), e("3s3szzN6iiNvNaN6NsNzDC3CNai3NiNKNv"), t("3s3szUNKNvNKzzzaDC3CNai3NiNKNv"), t("3s3szUi3i5NKN5"), i("3DN3NsNazzN6iiNvNaN6NsNzN3iDDC3CNai3NiNKNv"), e("3DN6NDNaN6ijDCzaNsi3NvN5NjN3iDDC3CNai3NiNKNv"), r("3DN6N5NmzUN3NaizDC33iCNzNsizN3"), t("35NsNNN3iDDC33iCNzNsizN3"), e("35NsNNN335N3NsiDN5Nj"), n("35N5iDNKiCizNKNvNiDvzzNKN5izNKN6NvNsiDiK"), e("35N3NNz5NaNKN3NvizDC3CNai3NiNKNv"), n("35NjN3NaNaDv33zKzjN3NaiCN3iD"), e("35NKNaiNN3iDNaNKNiNjizDC3CNai3NiDUzKNv"), n("35NKNUiCNaN3DC3CNsi5i5"), e("35NmiKiCN3DC3iN3NDDC3CNai3NiNKNv"), t("35i3NUNsiziDNs3CzzzNDCzDiDN6iii5N3iDDC3CNai3NiNKNv"), n("35iKNUNsNvizN3N5DC3CzmzKDCz5NaNKN3Nviz"), n("3zN3NvN5N3NvizDCzN3zzvDCiCNai3NiDUNKNv"), t("3zNji3NvNzN3iDDCzzNsiCz5iziDNaDCzv3Czs3CzKDC3CNai3NiNKNv"), t("3zN6iDN5NjzjN3NaiCN3iD"), r("33NvNKiziKDC3CNaNsiKN3iD"), i("33iCNaNsiKDC3Cz5"), i("3NzzN6iiNvNaN6NsNzN3iD"), i("3NN3N3izNaN3DC3z3NDCz5N6iDN3"), n("3Nzaz5DCzUi3NaizNKNUN3NzNKNsDC3CNai3NiNKNv"), i("3iN3NDDCz5N6NUiCN6NvN3Nvizi5"), n("3iN3NDzmNKizDUNKNvizN3NiiDNKN3iDizN3DC3CzzzN"), n("3iz3zD39z3zvDCzDiDN6iii5N3iDDCz3ijizN3Nvi5NKN6Nv"), e("3iN6NaNNiDNsNUDCzUNsizNjN3NUNsizNKN5Ns"), n("3iN6iDNzz5NsiCizi3iDN33j"), t("3i3CzKDCzzN3izN3N5izN6iDDC5sDv5z"), t("3KNsNvNzN3ijDCzUN3NzNKNsDC3CNai3NiNKNv"), t("3KNsNvNzN3ijDC3CzzzNDC3NNKN3iiN3iD"), r("3KN6i33zi3NDN3DC3CNai3NiDUNKNv"), e("i9NsNmN6")],
                            s = [],
                            a = {};
                        s.push(o(h[r("iCNai3NiNKNvi5")],
                            function(e) {
                                a[e.name] = 1;
                                var r = o(e,
                                    function(t) {
                                        return [t.type, t.suffixes].join(n("iv"))
                                    }).join(i("Da"));
                                return [e.name, e.description, r].join(t("5959"))
                            },
                            this).join(i("Dz")));
                        s.push(o(_,
                            function(n) {
                                if (a[n]) return r("");
                                n = h[t("iCNai3NiNKNvi5")][n];
                                if (!n) return i("");
                                var s = o(n,
                                    function(e) {
                                        return [e.type, e.suffixes].join(t("iv"))
                                    }).join(i("Da"));
                                return [n.name, n.description, s].join(e("5959"))
                            },
                            this).join(e("5m")));
                        return s.join(r("5m"))
                    }
                    function l() {
                        if (window[n("zsN5izNKiNN33jz6NDN9N3N5iz")]) {
                            var s = [n("zsN5iDN63CzzzNDv3CzzzN"), e("zsNzN6NzNDDv35iziDN3NsNU"), e("zsNiz5N6NviziDN6NaDvzsNiz5N6NviziDN6Na"), r("zzN3iNNsNa3N3D3jz5iziDNaDvzzN3iNNsNa3N3D3jz5iziDNaDv5s"), n("zUNsN5iDN6NUN3NzNKNszNNaNsi5Nj3CNsiCN3iDDvzUNsN5iDN6NUN3NzNKNszNNaNsi5Nj3CNsiCN3iD"), e("zUi5ijNUNa5DDvzzz6zUzzN6N5i3NUN3Nviz"), r("zUi5ijNUNa5DDv3jzUzazj3z3z3C"), t("3CzzzNDv3CNzNNz5iziDNa"), e("3si3NKN5Nm3zNKNUN3Dv3si3NKN5Nm3zNKNUN3"), e("3si3NKN5Nm3zNKNUN3z5NjN3N5Nmz6NDN9N3N5izDv3si3NKN5Nm3zNKNUN3z5NjN3N5NmDv5s"), e("iDNUN6N5ijDv3DN3NsNa3CNaNsiKN3iDDCzi5DDCz5N6NviziDN6Na"), e("iDNUN6N5ijDv3DN3NsNa3CNaNsiKN3iDDCzi5DDCz5N6NviziDN6NaDv5s"), t("3DN3NsNa3CNaNsiKN3iD"), i("3DN3NsNa3CNaNsiKN3iDDv3DN3NsNa3CNaNsiKN3iDDjizNUDKDCzsN5izNKiNN33jDCz5N6NviziDN6NaDCDj555DDUNDNKizDK"), r("3DN3NsNa3NNKNzN3N6Dv3DN3NsNa3NNKNzN3N6DjizNUDKDCzsN5izNKiNN33jDCz5N6NviziDN6NaDCDj555DDUNDNKizDK"), i("iDNUN6N5ijDv3DN3NsNa3CNaNsiKN3iDDCzi5DDCz5N6NviziDN6Na"), t("35N5iDNKiCizNKNvNiDvzzNKN5izNKN6NvNsiDiK"), i("35NjN3NaNaDv33zKzjN3NaiCN3iD"), r("35NjN6N5NmiiNsiNN3zNNaNsi5NjDv35NjN6N5NmiiNsiNN3zNNaNsi5Nj"), i("353iz5izNaDv353iz5izNa"), e("35NmiKiCN3DvzzN3izN3N5izNKN6Nv"), i("3zzzz5z5izNaDv3zzzz5z5izNa"), r("3izU3CNaNsiKN3iDDvz6z53j")];
                            return o(s,
                                function(t) {
                                    try {
                                        return new(window[e("zsN5izNKiNN33jz6NDN9N3N5iz")])(t),
                                            t
                                    } catch(i) {
                                        return null
                                    }
                                }).join(n("5m"))
                        }
                        return n("")
                    }
                    function g() {
                        try {
                            return !! window[n("i5N3i5i5NKN6Nv35izN6iDNsNiN3")]
                        } catch(t) {
                            return ! 0
                        }
                    }
                    function $() {
                        try {
                            return !! window[r("NaN6N5NsNa35izN6iDNsNiN3")]
                        } catch(t) {
                            return ! 0
                        }
                    }
                    function o(t, i, n) {
                        var e = [];
                        if (null == t) return e;
                        if (u && t.map === u) return t.map(i, n);
                        b(t,
                            function(t, r, s) {
                                e[e.length] = i.call(n, t, r, s)
                            });
                        return e
                    }
                    function b(t, i) {
                        if (null !== t) if (c && t.forEach === c) t.forEach(i, void 0);
                        else if (t.length === +t.length) for (var e = 0,
                                                                  n = t.length; n > e && i.call(void 0, t[e], e, t) !== {}; e++);
                        else for (e in t) if (t.hasOwnProperty(e) && i.call(void 0, t[e], e, t) === {}) break
                    }
                    var c = Array.prototype.forEach,
                        u = Array.prototype.map,
                        s = {
                            e: m,
                            j: !0,
                            i: !0,
                            h: !0,
                            b: !0,
                            a: !0
                        };
                    typeof a == r("NNi3NvN5izNKN6Nv") ? s.e = a: (null != a.b && void 0 != a.b && (s.b = a.b), null != a.a && void 0 != a.a && (s.a = a.a));
                    this.get = function() {
                        var a = [],
                            c = [];
                        if (it) {
                            a.push(g());
                            a.push($());
                            a.push( !! window[e("NKNvNzN3ijN3NzzzzD")]);
                            _.body ? a.push(typeof _.body[n("NsNzNzzDN3NjNsiNNKN6iD")]) : a.push("undefined");
                            a.push(typeof window[i("N6iCN3NvzzNsizNsNDNsi5N3")]);
                            a.push(h[i("N5iCi3z5NaNsi5i5")]);
                            a.push(h[i("iCNaNsizNNN6iDNU")]);
                            var o;
                            if (o = s.i) try {
                                var u = _.createElement(r("N5NsNviNNsi5"));
                                o = !(!u[t("NiN3izz5N6NvizN3ijiz")] || !u[n("NiN3izz5N6NvizN3ijiz")](r("5DNz")))
                            } catch(l) {
                                o = !1
                            }
                            if (o) try {
                                a.push(f()),
                                s.b && a.push(v())
                            } catch(N) {
                                a.push(t("N5NsNviNNsi5DCN3ijN5N3iCizNKN6Nv"))
                            }
                            a.push(E());
                            s.a && c.push(p());
                            c.push(h[i("i3i5N3iDzsNiN3Nviz")]);
                            c.push(h[n("NaNsNvNii3NsNiN3")]);
                            c.push(window[r("i5N5iDN3N3Nv")][e("N5N6NaN6iDzzN3iCizNj")]);
                            s.j && (o = window[i("i5N5iDN3N3Nv")] ? [window[e("i5N5iDN3N3Nv")].height, window[t("i5N5iDN3N3Nv")].width] : [0, 0], typeof o !== t("i3NvNzN3NNNKNvN3Nz") && c.push(o.join(t("ij"))));
                            c.push((new Date)[n("NiN3iz3zNKNUN3i9N6NvN3z6NNNNi5N3iz")]());
                            c.push(h[n("NzN6zvN6iz3ziDNsN5Nm")]);
                            c.push(d())
                        }
                        o = [];
                        s.e ? (o.push(s.e(a.join(i("D5D5D5")))), o.push(s.e(c.join(t("D5D5D5"))))) : (o.push(m(a.join(e("D5D5D5")))), o.push(m(c.join(r("D5D5D5")))));
                        return o
                    }
                }
                function m(_) {
                    var u, c, s, a, o;
                    u = 3 & _.length;
                    c = _.length - u;
                    s = 31;
                    for (o = 0; c > o;) a = 255 & _.charCodeAt(o) | (255 & _.charCodeAt(++o)) << 8 | (255 & _.charCodeAt(++o)) << 16 | (255 & _.charCodeAt(++o)) << 24,
                        ++o,
                        a = 3432918353 * (65535 & a) + ((3432918353 * (a >>> 16) & 65535) << 16) & 4294967295,
                        a = a << 15 | a >>> 17,
                        a = 461845907 * (65535 & a) + ((461845907 * (a >>> 16) & 65535) << 16) & 4294967295,
                        s ^= a,
                        s = s << 13 | s >>> 19,
                        s = 5 * (65535 & s) + ((5 * (s >>> 16) & 65535) << 16) & 4294967295,
                        s = (65535 & s) + 27492 + (((s >>> 16) + 58964 & 65535) << 16);
                    a = 0;
                    switch (u) {
                        case 3:
                            a ^= (255 & _.charCodeAt(o + 2)) << 16;
                        case 2:
                            a ^= (255 & _.charCodeAt(o + 1)) << 8;
                        case 1:
                            a ^= 255 & _.charCodeAt(o),
                                a = 3432918353 * (65535 & a) + ((3432918353 * (a >>> 16) & 65535) << 16) & 4294967295,
                                a = a << 15 | a >>> 17,
                                s ^= 461845907 * (65535 & a) + ((461845907 * (a >>> 16) & 65535) << 16) & 4294967295
                    }
                    s ^= _.length;
                    s ^= s >>> 16;
                    s = 2246822507 * (65535 & s) + ((2246822507 * (s >>> 16) & 65535) << 16) & 4294967295;
                    s ^= s >>> 13;
                    s = 3266489909 * (65535 & s) + ((3266489909 * (s >>> 16) & 65535) << 16) & 4294967295;
                    _ = (s ^ s >>> 16) >>> 0;
                    u = [];
                    u.push(_);
                    try {
                        var d, l = _ + r("");
                        for (s = o = c = 0; s < l.length; s++) try {
                            var h = parseInt(l.charAt(s) + n(""));
                            c = h || 0 === h ? c + h: c + 1;
                            o++
                        } catch(g) {
                            c += 1,
                                o++
                        }
                        d = W(1 * c / (0 == o ? 1 : o));
                        var m, p = Math.floor(d / Math.pow(10, 1)),
                            v = _ + r("");
                        for (s = o = c = h = l = 0; s < v.length; s++) try {
                            var f = parseInt(v.charAt(s) + t(""));
                            f || 0 === f ? p > f ? (h++, l += f) : (o++, c += f) : (o++, c += p)
                        } catch($) {
                            o++,
                                c += p
                        }
                        o = 0 == o ? 1 : o;
                        m = W(1 * c / o - 1 * l / (0 == h ? 1 : h));
                        u.push(L(d, r("5C")));
                        u.push(L(m, n("5C")))
                    } catch(N) {
                        u = [],
                            u.push(_),
                            u.push(E(i("DU")).join(r(""))),
                            u.push(E(t("DU")).join(e("")))
                    }
                    return u.join(r(""))
                }
                function W(t) {
                    if (0 > t || t >= 10) throw Error(i("5s5s5s5C"));
                    var s = E(e("5C"));
                    t = e("") + t;
                    for (var a = 0,
                             n = 0; a < s.length && n < t.length; n++) t.charAt(n) != i("Dv") && (s[a++] = t.charAt(n));
                    return parseInt(s.join(r("")))
                }
                function L(s, a) {
                    var t = n("") + s;
                    if (2 < t.length) throw Error(i("5s5s5s5s"));
                    if (2 == t.length) return t;
                    for (var e = [], r = t.length; 2 > r; r++) e.push(a);
                    e.push(t);
                    return e.join(n(""))
                }
                function E(i) {
                    for (var t = [], e = 0; 2 > e; e++) t.push(i);
                    return t
                }
                function a(t) {
                    return null == t || void 0 == t
                }
                function s(e, i, t) {
                    this.f = e;
                    this.c = i;
                    this.g = a(t) ? !0 : t
                }
                function ot(t) {
                    if (a(t) || a(t.f) || a(t.c)) return ! 1;
                    try {
                        if (a(window[t.f])) return ! 1
                    } catch(e) {
                        return ! 1
                    }
                    return ! 0
                }
                function c(t, r) {
                    if (a(t)) return n("");
                    for (var e = 0; e < t.length; e++) {
                        var i = t[e];
                        if (!a(i) && i.f == r) return i
                    }
                }
                function V() {
                    var _;
                    t: {
                        if (!a(o)) for (_ = 0; _ < o.length; _++) {
                            var g = o[_];
                            if (g.g && !ot(g)) {
                                _ = g;
                                break t
                            }
                        }
                        _ = null
                    }
                    var s;
                    if (a(_)) {
                        try {
                            s = 1.01 === window.parseFloat(r("5sDv5C5s")) && window.isNaN(window.parseFloat(i("zjz3zazaz6")))
                        } catch(w) {
                            s = !1
                        }
                        if (s) {
                            var l;
                            try {
                                l = 123 === window.parseInt(n("5s5D55")) && window.isNaN(window.parseInt(i("zjz3zazaz6")))
                            } catch(C) {
                                l = !1
                            }
                            if (l) {
                                var d;
                                try {
                                    d = window.decodeURI(r("D35D5D")) === r("DD")
                                } catch(y) {
                                    d = !1
                                }
                                if (d) {
                                    var m;
                                    try {
                                        m = window.decodeURIComponent(i("D35D5N")) === n("DN")
                                    } catch(N) {
                                        m = !1
                                    }
                                    if (m) {
                                        var u;
                                        try {
                                            u = window.encodeURI(i("DD")) === r("D35D5D")
                                        } catch($) {
                                            u = !1
                                        }
                                        if (u) {
                                            var v;
                                            try {
                                                v = window.encodeURIComponent(i("DN")) === t("D35D5N")
                                            } catch(b) {
                                                v = !1
                                            }
                                            if (v) {
                                                var p;
                                                try {
                                                    p = window.escape(r("DN")) === n("D35D5N")
                                                } catch(E) {
                                                    p = !1
                                                }
                                                if (p) {
                                                    var f;
                                                    try {
                                                        f = window.unescape(n("D35D5N")) === i("DN")
                                                    } catch(T) {
                                                        f = !1
                                                    }
                                                    if (f) {
                                                        var h;
                                                        try {
                                                            h = 123 === window.eval(t("DjNNi3NvN5izNKN6NvDjDKimiDN3izi3iDNvDC5s5D555miUDKDjDK5m"))
                                                        } catch(x) {
                                                            h = !1
                                                        }
                                                        s = h ? null: c(o, r("N3iNNsNa"))
                                                    } else s = c(o, n("i3NvN3i5N5NsiCN3"))
                                                } else s = c(o, r("N3i5N5NsiCN3"))
                                            } else s = c(o, r("N3NvN5N6NzN3333DzKz5N6NUiCN6NvN3Nviz"))
                                        } else s = c(o, e("N3NvN5N6NzN3333DzK"))
                                    } else s = c(o, r("NzN3N5N6NzN3333DzKz5N6NUiCN6NvN3Nviz"))
                                } else s = c(o, n("NzN3N5N6NzN3333DzK"))
                            } else s = c(o, e("iCNsiDi5N3zKNviz"))
                        } else s = c(o, t("iCNsiDi5N3zNNaN6Nsiz"))
                    } else s = _;
                    return s
                }
                function dt() {
                    var t = V();
                    if (!a(t)) return t.c;
                    try {
                        t = a(window[r("iCNjNsNvizN6NU")]) || a(window[r("iCNjNsNvizN6NU")][e("NKNvN9N3N5izz9i5")]) ? null: c(o, i("iCNjNsNvizN6NUDvNKNvN9N3N5izz9i5"))
                    } catch(s) {
                        t = null
                    }
                    if (!a(t)) return t.c;
                    try {
                        t = a(context) || a(context[e("NjNsi5Njz5N6NzN3")]) ? null: c(o, n("N5N6NvizN3ijizDvNjNsi5Njz5N6NzN3"))
                    } catch(_) {
                        t = null
                    }
                    return a(t) ? null: t.c
                }
                function J() {
                    for (var t = [], i = 0; 3 > i; i++) {
                        var n = Math.random() * st,
                            n = Math.floor(n);
                        t.push(B.charAt(n))
                    }
                    return t.join(e(""))
                }
                function b(s) {
                    for (var n = (_[t("N5N6N6NmNKN3")] || i("")).split(t("5mDC")), e = 0; e < n.length; e++) {
                        var r = n[e].indexOf(i("5U"));
                        if (r >= 0) {
                            var a = n[e].substring(r + 1, n[e].length);
                            if (n[e].substring(0, r) == s) return window.decodeURIComponent(a)
                        }
                    }
                    return null
                }
                function M(o) {
                    var c = [e("iN"), t("NNiC"), e("i3"), e("Nj"), t("N3N5"), r("N3NU"), e("NKN5iC")],
                        s = e("");
                    if (null == o || void 0 == o) return o;
                    if (typeof o == [t("N6ND"), i("N9N3"), i("N5iz")].join(n(""))) {
                        for (var s = s + t("im"), _ = 0; _ < c.length; _++) if (o.hasOwnProperty(c[_])) {
                            var u = n("Di") + c[_] + r("Di59Di"),
                                a;
                            a = t("") + o[c[_]];
                            a = null == a || void 0 == a ? a: a.replace(/'/g, e("3aDi")).replace(/"/g, t("DD"));
                            s += u + a + i("DiDa")
                        }
                        s.charAt(s.length - 1) == n("Da") && (s = s.substring(0, s.length - 1));
                        return s += e("iU")
                    }
                    return null
                }
                function y(s, o, c, a) {
                    var r = [];
                    r.push(s + t("5U") + encodeURIComponent(o));
                    c && (s = new Date, s = new Date(a), a = s[e("izN6zizU3z35iziDNKNvNi")](), r.push(n("5mDC")), r.push(t("N3ij")), r.push(t("iCNK")), r.push(n("iDN3")), r.push(e("i55U")), r.push(a));
                    r.push(n("5mDC"));
                    r.push(i("iCNs"));
                    r.push(t("izNj5UD6"));
                    null != l && void 0 != l && l != t("") && (r.push(i("5mDC")), r.push(e("NzN6")), r.push(i("NUNsNK")), r.push(t("Nv5U")), r.push(l));
                    _[i("N5N6N6NmNKN3")] = r.join(t(""))
                }
                function O(t) {
                    window[T] = t
                }
                function z(t) {
                    window[k] = t
                }
                function j(i) {
                    for (var t = [], e = 0; 10 > e; e++) t.push(i);
                    return t.join(n(""))
                }
                function Z(e, i) {
                    var t = b(e);
                    null !== t && void 0 !== t && t !== n("") || y(e, i, !1)
                }
                function A() {
                    var t = b(p);
                    if (null == t || void 0 == t || t == r("")) t = window[k];
                    return t
                }
                function rt() {
                    var e = A();
                    if (null == e || void 0 == e || e == t("")) return ! 1;
                    try {
                        return (e = parseInt(e)) && e >= g ? !0 : !1
                    } catch(i) {
                        return ! 1
                    }
                }
                function P(t) {
                    if (null == t || void 0 == t || t == i("")) return null;
                    t = t.split(e("59"));
                    return 2 > t.length || !/[0-9]+/gi.test(t[1]) ? null: parseInt(t[1])
                }
                function C() {
                    var t = b(d);
                    if (null == t || void 0 == t || t == e("")) t = window[T];
                    return t
                }
                function nt() {
                    var i = C();
                    if (null == i || void 0 == i || i == t("")) return 0;
                    i = P(i);
                    return null == i ? 0 : i - (N - S) - (new(window[t("zzNsizN3")]))[e("NiN3iz3zNKNUN3")]()
                }
                function U(a, n) {
                    var s = new(window[i("zzNsizN3")]);
                    s[i("i5N3iz3zNKNUN3")](s[r("NiN3iz3zNKNUN3")]() - 1e4);
                    null == n || void 0 == n || n == i("") ? window[i("NzN6N5i3NUN3Nviz")][e("N5N6N6NmNKN3")] = a + i("5UNvi3NaNa5mDCiCNsizNj5UD65mDCN3ijiCNKiDN3i55U") + s[t("izN6zizU3z35iziDNKNvNi")]() : window[i("NzN6N5i3NUN3Nviz")][r("N5N6N6NmNKN3")] = a + i("5UNvi3NaNa5mDCiCNsizNj5UD65mDCNzN6NUNsNKNv5U") + n + e("5mDCN3ijiCNKiDN3i55U") + s[t("izN6zizU3z35iziDNKNvNi")]()
                }
                function F() {
                    if (! (null == f || void 0 == f || 0 >= f.length)) for (var i = 0; i < f.length; i++) {
                        var e = f[i]; (null != l && void 0 != l && l != t("") || null != e && void 0 != e && e != t("")) && l != e && (U(d, e), U(p, e))
                    }
                }
                function w() {
                    F();
                    window[k] = null;
                    window[T] = null;
                    var C = !0,
                        s = {
                            v: t("iN5sDv5s")
                        },
                        l = dt();
                    l && (s[e("NKN5iC")] = l);
                    l = null;
                    s[i("Nj")] = et;
                    var P = (new(window[r("zzNsizN3")]))[n("NiN3iz3zNKNUN3")]() + N,
                        Q = P + 15768e7;
                    s[e("i3")] = J() + P + J();
                    try {
                        var o = new ut({
                            b: ct,
                            a: _t
                        }).get();
                        null != o && void 0 != o && 0 < o.length ? s[n("NNiC")] = o.join(t("Da")) : (s[r("NNiC")] = j(n("5C")), s[r("N3N5")] = t("5s"), C = !1)
                    } catch(pt) {
                        s[n("NNiC")] = j(r("5C")),
                            s[e("N3N5")] = t("5s"),
                            C = !1
                    }
                    try {
                        var $ = l = M(s),
                            s = tt;
                        if (null == s || void 0 == s) throw Error(e("5s5C5C5j"));
                        if (null == $ || void 0 == $) $ = i("");
                        var o = $,
                            _;
                        _ = null == $ ? H([]) : H(I($));
                        var a = I(o + _),
                            L = I(s);
                        null == a && (a = []);
                        _ = [];
                        for (s = 0; 4 > s; s++) {
                            var E = 256 * Math.random(),
                                E = Math.floor(E);
                            _[s] = u(E)
                        }
                        var L = D(L),
                            L = x(L, D(_)),
                            E = L = D(L),
                            m;
                        if (null == a || void 0 == a || 0 == a.length) m = X();
                        else {
                            var c = a.length,
                                s = 0,
                                s = 60 >= c % 64 ? 64 - c % 64 - 4 : 128 - c % 64 - 4,
                                o = [];
                            v(a, o, 0, c);
                            for (a = 0; s > a; a++) o[c + a] = 0;
                            v(q(c), o, c + s, 4);
                            m = o
                        }
                        c = m;
                        if (null == c || 0 != c.length % 64) throw Error(t("5s5C5C53"));
                        m = [];
                        for (var a = 0,
                                 h = c.length / 64,
                                 s = 0; h > s; s++) for (m[s] = [], o = 0; 64 > o; o++) m[s][o] = c[a++];
                        h = [];
                        v(_, h, 0, 4);
                        for (var b = m.length,
                                 c = 0; b > c; c++) {
                            var f, U;
                            var V = m[c];
                            if (null == V) U = null;
                            else {
                                var nt = u( - 10);
                                _ = [];
                                for (var rt = V.length,
                                         a = 0; rt > a; a++) _.push(ht(V[a], nt++));
                                U = _
                            }
                            _ = U;
                            if (null == _) f = null;
                            else {
                                for (var ot = u(11), a = [], it = _.length, s = 0; it > s; s++) a.push(K(_[s], ot--));
                                f = a
                            }
                            var Y = x(f, L),
                                A;
                            _ = Y;
                            a = E;
                            if (null == _) A = null;
                            else if (null == a) A = _;
                            else {
                                for (var s = [], st = a.length, o = 0, at = _.length; at > o; o++) s[o] = u(_[o] + a[o % st]);
                                A = s
                            }
                            var Y = x(A, E),
                                W = R(Y),
                                W = R(W);
                            v(W, h, 64 * c + 4, 64);
                            E = W
                        }
                        var B;
                        if (null == h || void 0 == h) B = null;
                        else if (0 == h.length) B = r("");
                        else try {
                                b = [];
                                for (f = 0; f < h.length;) if (f + 3 <= h.length) b.push(G(h, f, 3)),
                                    f += 3;
                                else {
                                    b.push(G(h, f, h.length - f));
                                    break
                                }
                                B = b.join(e(""))
                            } catch(ft) {
                                throw Error(e("5s5C5s5C"))
                            }
                        l = B
                    } catch(lt) {
                        l = M({
                            ec: e("5D"),
                            em: lt.message
                        }),
                            C = !1
                    }
                    l = l + i("59") + P;
                    y(d, l, C, Q);
                    Z(d, l);
                    O(l);
                    y(p, g, C, Q);
                    Z(p, g);
                    z(g);
                    window[r("i5N3iz3zNKNUN3N6i3iz")] && window[n("i5N3iz3zNKNUN3N6i3iz")](w, S)
                }
                s.prototype = {
                    toString: function() {
                        return e("imDiNvNsNUN3Di59") + this.f + n("DaDCDiN5N6NzN3Di59") + this.c + n("DaDCDiNDiDN6iii5N3iD3CiDN6iCDi59") + this.g + r("iU")
                    }
                };
                var o = [new s(r("iiNKNvNzN6ii"), r("5C5C5C5C")), new s(e("NzN6N5i3NUN3Nviz"), n("5C5C5C5s")), new s(n("NvNsiNNKNiNsizN6iD"), i("5C5C5C5D")), new s(e("NaN6N5NsizNKN6Nv"), t("5C5C5C55")), new s(e("NjNKi5izN6iDiK"), n("5C5C5C5z")), new s(n("i5N5iDN3N3Nv"), t("5C5C5C5i")), new s(n("iCNsiDN3Nviz"), r("5C5C5C5j")), new s(r("izN6iC"), i("5C5C5C5K")), new s(n("i5N3NaNN"), r("5C5C5s5C")), new s(t("iCNsiDi5N3zNNaN6Nsiz"), t("5C5s5C5C")), new s(t("iCNsiDi5N3zKNviz"), i("5C5s5C5s")), new s(t("NzN3N5N6NzN3333DzK"), e("5C5s5C5D")), new s(e("NzN3N5N6NzN3333DzKz5N6NUiCN6NvN3Nviz"), i("5C5s5C55")), new s(t("N3NvN5N6NzN3333DzK"), e("5C5s5C5z")), new s(n("N3NvN5N6NzN3333DzKz5N6NUiCN6NvN3Nviz"), e("5C5s5C53")), new s(i("N3i5N5NsiCN3"), e("5C5s5C5N")), new s(n("i3NvN3i5N5NsiCN3"), r("5C5s5C5i")), new s(n("N3iNNsNa"), e("5C5s5C5j")), new s(e("36iCNjNsNvizN6NU"), n("5C5D5C5C"), !1), new s(t("N5NsNaNa3CNjNsNvizN6NU"), e("5C5D5C5s"), !1), new s(i("iCNjNsNvizN6NU"), n("5C5D5C5D"), !1), new s(e("iCNjNsNvizN6NUDvNKNvN9N3N5izz9i5"), t("5C5D5C55"), !1), new s(i("N5N6NvizN3ijizDvNjNsi5Njz5N6NzN3"), n("5C5D5s5s"), !1)],
                    it = V() ? !1 : !0,
                    et = window && window[r("NaN6N5NsizNKN6Nv")] && window[e("NaN6N5NsizNKN6Nv")].host || r("NvN6iz36N3ijNKi5iz36NjN6i5iz"),
                    _ = window[e("NzN6N5i3NUN3Nviz")],
                    h = window[e("NvNsiNNKNiNsizN6iD")],
                    Q = [i("5C"), t("5s"), i("5D"), t("55"), t("5z"), e("53"), r("5N"), n("5i"), r("5j"), n("5K"), n("Ns"), e("ND"), i("N5"), t("Nz"), r("N3"), t("NN")],
                    Y = [0, 1996959894, 3993919788, 2567524794, 124634137, 1886057615, 3915621685, 2657392035, 249268274, 2044508324, 3772115230, 2547177864, 162941995, 2125561021, 3887607047, 2428444049, 498536548, 1789927666, 4089016648, 2227061214, 450548861, 1843258603, 4107580753, 2211677639, 325883990, 1684777152, 4251122042, 2321926636, 335633487, 1661365465, 4195302755, 2366115317, 997073096, 1281953886, 3579855332, 2724688242, 1006888145, 1258607687, 3524101629, 2768942443, 901097722, 1119000684, 3686517206, 2898065728, 853044451, 1172266101, 3705015759, 2882616665, 651767980, 1373503546, 3369554304, 3218104598, 565507253, 1454621731, 3485111705, 3099436303, 671266974, 1594198024, 3322730930, 2970347812, 795835527, 1483230225, 3244367275, 3060149565, 1994146192, 31158534, 2563907772, 4023717930, 1907459465, 112637215, 2680153253, 3904427059, 2013776290, 251722036, 2517215374, 3775830040, 2137656763, 141376813, 2439277719, 3865271297, 1802195444, 476864866, 2238001368, 4066508878, 1812370925, 453092731, 2181625025, 4111451223, 1706088902, 314042704, 2344532202, 4240017532, 1658658271, 366619977, 2362670323, 4224994405, 1303535960, 984961486, 2747007092, 3569037538, 1256170817, 1037604311, 2765210733, 3554079995, 1131014506, 879679996, 2909243462, 3663771856, 1141124467, 855842277, 2852801631, 3708648649, 1342533948, 654459306, 3188396048, 3373015174, 1466479909, 544179635, 3110523913, 3462522015, 1591671054, 702138776, 2966460450, 3352799412, 1504918807, 783551873, 3082640443, 3233442989, 3988292384, 2596254646, 62317068, 1957810842, 3939845945, 2647816111, 81470997, 1943803523, 3814918930, 2489596804, 225274430, 2053790376, 3826175755, 2466906013, 167816743, 2097651377, 4027552580, 2265490386, 503444072, 1762050814, 4150417245, 2154129355, 426522225, 1852507879, 4275313526, 2312317920, 282753626, 1742555852, 4189708143, 2394877945, 397917763, 1622183637, 3604390888, 2714866558, 953729732, 1340076626, 3518719985, 2797360999, 1068828381, 1219638859, 3624741850, 2936675148, 906185462, 1090812512, 3747672003, 2825379669, 829329135, 1181335161, 3412177804, 3160834842, 628085408, 1382605366, 3423369109, 3138078467, 570562233, 1426400815, 3317316542, 2998733608, 733239954, 1555261956, 3268935591, 3050360625, 752459403, 1541320221, 2607071920, 3965973030, 1969922972, 40735498, 2617837225, 3943577151, 1913087877, 83908371, 2512341634, 3803740692, 2075208622, 213261112, 2463272603, 3855990285, 2094854071, 198958881, 2262029012, 4057260610, 1759359992, 534414190, 2176718541, 4139329115, 1873836001, 414664567, 2282248934, 4279200368, 1711684554, 285281116, 2405801727, 4167216745, 1634467795, 376229701, 2685067896, 3608007406, 1308918612, 956543938, 2808555105, 3495958263, 1231636301, 1047427035, 2932959818, 3654703836, 1088359270, 936918e3, 2847714899, 3736837829, 1202900863, 817233897, 3183342108, 3401237130, 1404277552, 615818150, 3134207493, 3453421203, 1423857449, 601450431, 3009837614, 3294710456, 1567103746, 711928724, 3020668471, 3272380065, 1510334235, 755167117],
                    at = [45, -10, 81, 2, 0, 90, -24, 96, 119, -51, -104, 19, 102, 74, -8, 94, -22, -99, -17, -89, -126, -31, -40, 1, -107, -68, -32, 116, 15, -13, -95, 126, -34, 107, -47, 11, 88, -28, -74, -57, -81, 122, 123, 120, 56, 76, -82, -85, -54, -76, -5, 50, -44, -16, 99, 53, 36, -83, 23, -101, -7, 113, 115, -78, -120, 92, -50, 111, -2, 114, -121, 47, -20, 38, -38, -60, -124, -56, -55, 25, 84, 70, -52, -62, 106, -14, 14, -46, 77, 86, 10, 93, 7, -98, 34, -84, -33, -64, 32, 110, -41, -53, -45, 60, -25, -49, -48, -37, 78, -127, -122, -118, 63, 127, -69, 40, -35, -113, 100, 58, -30, 55, -70, -116, -86, 24, 4, 39, 33, 18, 83, -94, 54, -71, 44, -73, -108, 12, 79, -105, 57, 20, 67, 21, -111, -102, 43, 91, 62, -63, 13, 30, -23, -6, -87, -91, 5, 66, -90, -42, -77, 3, -115, -58, 26, 69, -97, -106, 82, -93, -61, -12, 49, -72, -123, 108, -79, -43, 121, 73, -88, -75, 42, 6, -9, -19, -11, -27, -67, 101, 80, -112, 87, 103, -125, -4, -26, 51, 104, 16, 64, 98, 125, -92, -65, 52, -117, 72, -66, 8, -36, -59, 35, -3, 17, 118, -96, 29, 117, 65, 48, 109, -39, 112, -110, 41, -119, 105, 89, -109, 97, 71, 61, -21, -29, -1, 31, -15, 37, -80, 85, -18, 59, -103, -128, 28, 95, -114, 22, 9, -100, 46, 124, 68, 75, 27],
                    tt = n("5s5z5i555zNN5N5CN55N535CND5s5zN5N5z5NDzD5z5N555jNDNNzzz55Czs5z55z5zzNz5j5z5CN3zNzs5z5DzzNsNs"),
                    d = r("z935z33535zKz6zvzKzzDU3i3K3z3j39zzza"),
                    p = e("36NKNjiziji9NzNKNaijNaNz3C5j36"),
                    g = 30,
                    B = t("Ns39ND3K5CN53jNz3i5sN33NNN5D33Ni553zNj5z35NK3D53N93sNm5N3CNaz65iNUzvNv5jzUN6za5KiCzmisz9iDzKi5zjizzii3zNiNz3iizzijz5iKzDi9zs"),
                    st = B.length,
                    N = 6e5,
                    S = 54e4,
                    _t = !1,
                    ct = !0,
                    l = t(""),
                    T = d.replace(/[^a-zA-Z0-9$]/g, e("")).toLowerCase(),
                    k = p.replace(/[^a-zA-Z0-9$]/g, r("")).toLowerCase(),
                    $ = window && window[r("NaN6N5NsizNKN6Nv")] && window[r("NaN6N5NsizNKN6Nv")][t("NjN6i5izNvNsNUN3")] || e("NvN6iz36N3ijNKi5iz36NjN6i5izNvNsNUN3"),
                    f = function(e) {
                        var s = [];
                        if (!e) return s;
                        e = e.split(i("Dv"));
                        for (var a = t(""), n = 0; n < e.length; n++) n < e.length - 1 && (a = r("Dv") + e[e.length - 1 - n] + a, s.push(a));
                        return s
                    } ($);
                f.push(null);
                f.push(r("Dv") + $);
                1 <
                function(c) {
                    for (var a = 0,
                             s = (_[i("N5N6N6NmNKN3")] || n("")).split(r("5mDC")), t = 0; t < s.length; t++) {
                        var o = s[t].indexOf(e("5U"));
                        o >= 0 && s[t].substring(0, o) == c && (a += 1)
                    }
                    return a
                } (d) && F(); !
                    function() {
                        var t = C();
                        if (null == t || void 0 == t || t == i("")) t = !1;
                        else {
                            var n;
                            if (n = rt()) t = P(t),
                                n = !(null == t || t - (new(window[i("zzNsizN3")]))[e("NiN3iz3zNKNUN3")]() <= N - S);
                            t = n
                        }
                        return t
                    } () ? w() : (O(C()), z(A()), $ = nt(), window[r("i5N3iz3zNKNUN3N6i3iz")] && window[t("i5N3iz3zNKNUN3N6i3iz")](w, $))
            } ()
    } ();
var dbits;
var canary = 0xdeadbeefcafe;
var j_lm = 15715070 == (16777215 & canary);
function BigInteger(t, e, i) {
    if (null != t) if ("number" == typeof t) this.fromNumber(t, e, i);
    else if (null == e && "string" != typeof t) this.fromString(t, 256);
    else this.fromString(t, e)
}
function nbi() {
    return new BigInteger(null)
}
function am1(r, s, e, i, t, a) {
    for (; --a >= 0;) {
        var n = s * this[r++] + e[i] + t;
        t = Math.floor(n / 67108864);
        e[i++] = 67108863 & n
    }
    return t
}
function am2(i, s, _, n, e, u) {
    var a = 32767 & s,
        o = s >> 15;
    for (; --u >= 0;) {
        var t = 32767 & this[i];
        var c = this[i++] >> 15;
        var r = o * t + c * a;
        t = a * t + ((32767 & r) << 15) + _[n] + (1073741823 & e);
        e = (t >>> 30) + (r >>> 15) + o * c + (e >>> 30);
        _[n++] = 1073741823 & t
    }
    return e
}
function am3(i, s, _, n, e, u) {
    var a = 16383 & s,
        o = s >> 14;
    for (; --u >= 0;) {
        var t = 16383 & this[i];
        var c = this[i++] >> 14;
        var r = o * t + c * a;
        t = a * t + ((16383 & r) << 14) + _[n] + e;
        e = (t >> 28) + (r >> 14) + o * c;
        _[n++] = 268435455 & t
    }
    return e
}
if (j_lm && "Microsoft Internet Explorer" == navigator.appName) {
    BigInteger.prototype.am = am2;
    dbits = 30
} else if (j_lm && "Netscape" != navigator.appName) {
    BigInteger.prototype.am = am1;
    dbits = 26
} else {
    BigInteger.prototype.am = am3;
    dbits = 28
}
BigInteger.prototype.DB = dbits;
BigInteger.prototype.DM = (1 << dbits) - 1;
BigInteger.prototype.DV = 1 << dbits;
var BI_FP = 52;
BigInteger.prototype.FV = Math.pow(2, BI_FP);
BigInteger.prototype.F1 = BI_FP - dbits;
BigInteger.prototype.F2 = 2 * dbits - BI_FP;
var BI_RM = "0123456789abcdefghijklmnopqrstuvwxyz";
var BI_RC = new Array;
var rr, vv;
rr = "0".charCodeAt(0);
for (vv = 0; 9 >= vv; ++vv) BI_RC[rr++] = vv;
rr = "a".charCodeAt(0);
for (vv = 10; 36 > vv; ++vv) BI_RC[rr++] = vv;
rr = "A".charCodeAt(0);
for (vv = 10; 36 > vv; ++vv) BI_RC[rr++] = vv;
function int2char(t) {
    return BI_RM.charAt(t)
}
function intAt(e, i) {
    var t = BI_RC[e.charCodeAt(i)];
    return null == t ? -1 : t
}
function bnpCopyTo(e) {
    for (var t = this.t - 1; t >= 0; --t) e[t] = this[t];
    e.t = this.t;
    e.s = this.s
}
function bnpFromInt(t) {
    this.t = 1;
    this.s = 0 > t ? -1 : 0;
    if (t > 0) this[0] = t;
    else if ( - 1 > t) this[0] = t + DV;
    else this.t = 0
}
function nbv(e) {
    var t = nbi();
    t.fromInt(e);
    return t
}
function bnpFromString(n, i) {
    var e;
    if (16 == i) e = 4;
    else if (8 == i) e = 3;
    else if (256 == i) e = 8;
    else if (2 == i) e = 1;
    else if (32 == i) e = 5;
    else if (4 == i) e = 2;
    else {
        this.fromRadix(n, i);
        return
    }
    this.t = 0;
    this.s = 0;
    var s = n.length,
        a = !1,
        t = 0;
    for (; --s >= 0;) {
        var r = 8 == e ? 255 & n[s] : intAt(n, s);
        if (! (0 > r)) {
            a = !1;
            if (0 == t) this[this.t++] = r;
            else if (t + e > this.DB) {
                this[this.t - 1] |= (r & (1 << this.DB - t) - 1) << t;
                this[this.t++] = r >> this.DB - t
            } else this[this.t - 1] |= r << t;
            t += e;
            if (t >= this.DB) t -= this.DB
        } else if ("-" == n.charAt(s)) a = !0
    }
    if (8 == e && 0 != (128 & n[0])) {
        this.s = -1;
        if (t > 0) this[this.t - 1] |= (1 << this.DB - t) - 1 << t
    }
    this.clamp();
    if (a) BigInteger.ZERO.subTo(this, this)
}
function bnpClamp() {
    var t = this.s & this.DM;
    for (; this.t > 0 && this[this.t - 1] == t;)--this.t
}
function bnToString(n) {
    if (this.s < 0) return "-" + this.negate().toString(n);
    var t;
    if (16 == n) t = 4;
    else if (8 == n) t = 3;
    else if (2 == n) t = 1;
    else if (32 == n) t = 5;
    else if (4 == n) t = 2;
    else return this.toRadix(n);
    var o = (1 << t) - 1,
        r,
        s = !1,
        a = "",
        i = this.t;
    var e = this.DB - i * this.DB % t;
    if (i-->0) {
        if (e < this.DB && (r = this[i] >> e) > 0) {
            s = !0;
            a = int2char(r)
        }
        for (; i >= 0;) {
            if (t > e) {
                r = (this[i] & (1 << e) - 1) << t - e;
                r |= this[--i] >> (e += this.DB - t)
            } else {
                r = this[i] >> (e -= t) & o;
                if (0 >= e) {
                    e += this.DB; --i
                }
            }
            if (r > 0) s = !0;
            if (s) a += int2char(r)
        }
    }
    return s ? a: "0"
}
function bnNegate() {
    var t = nbi();
    BigInteger.ZERO.subTo(this, t);
    return t
}
function bnAbs() {
    return this.s < 0 ? this.negate() : this
}
function bnCompareTo(i) {
    var t = this.s - i.s;
    if (0 != t) return t;
    var e = this.t;
    t = e - i.t;
    if (0 != t) return this.s < 0 ? -t: t;
    for (; --e >= 0;) if (0 != (t = this[e] - i[e])) return t;
    return 0
}
function nbits(t) {
    var i = 1,
        e;
    if (0 != (e = t >>> 16)) {
        t = e;
        i += 16
    }
    if (0 != (e = t >> 8)) {
        t = e;
        i += 8
    }
    if (0 != (e = t >> 4)) {
        t = e;
        i += 4
    }
    if (0 != (e = t >> 2)) {
        t = e;
        i += 2
    }
    if (0 != (e = t >> 1)) {
        t = e;
        i += 1
    }
    return i
}
function bnBitLength() {
    if (this.t <= 0) return 0;
    else return this.DB * (this.t - 1) + nbits(this[this.t - 1] ^ this.s & this.DM)
}
function bnpDLShiftTo(i, e) {
    var t;
    for (t = this.t - 1; t >= 0; --t) e[t + i] = this[t];
    for (t = i - 1; t >= 0; --t) e[t] = 0;
    e.t = this.t + i;
    e.s = this.s
}
function bnpDRShiftTo(e, i) {
    for (var t = e; t < this.t; ++t) i[t - e] = this[t];
    i.t = Math.max(this.t - e, 0);
    i.s = this.s
}
function bnpLShiftTo(s, e) {
    var n = s % this.DB;
    var a = this.DB - n;
    var o = (1 << a) - 1;
    var i = Math.floor(s / this.DB),
        r = this.s << n & this.DM,
        t;
    for (t = this.t - 1; t >= 0; --t) {
        e[t + i + 1] = this[t] >> a | r;
        r = (this[t] & o) << n
    }
    for (t = i - 1; t >= 0; --t) e[t] = 0;
    e[i] = r;
    e.t = this.t + i + 1;
    e.s = this.s;
    e.clamp()
}
function bnpRShiftTo(r, t) {
    t.s = this.s;
    var e = Math.floor(r / this.DB);
    if (! (e >= this.t)) {
        var n = r % this.DB;
        var s = this.DB - n;
        var a = (1 << n) - 1;
        t[0] = this[e] >> n;
        for (var i = e + 1; i < this.t; ++i) {
            t[i - e - 1] |= (this[i] & a) << s;
            t[i - e] = this[i] >> n
        }
        if (n > 0) t[this.t - e - 1] |= (this.s & a) << s;
        t.t = this.t - e;
        t.clamp()
    } else t.t = 0
}
function bnpSubTo(n, i) {
    var e = 0,
        t = 0,
        r = Math.min(n.t, this.t);
    for (; r > e;) {
        t += this[e] - n[e];
        i[e++] = t & this.DM;
        t >>= this.DB
    }
    if (n.t < this.t) {
        t -= n.s;
        for (; e < this.t;) {
            t += this[e];
            i[e++] = t & this.DM;
            t >>= this.DB
        }
        t += this.s
    } else {
        t += this.s;
        for (; e < n.t;) {
            t -= n[e];
            i[e++] = t & this.DM;
            t >>= this.DB
        }
        t -= n.s
    }
    i.s = 0 > t ? -1 : 0;
    if ( - 1 > t) i[e++] = this.DV + t;
    else if (t > 0) i[e++] = t;
    i.t = e;
    i.clamp()
}
function bnpMultiplyTo(r, e) {
    var i = this.abs(),
        n = r.abs();
    var t = i.t;
    e.t = t + n.t;
    for (; --t >= 0;) e[t] = 0;
    for (t = 0; t < n.t; ++t) e[t + i.t] = i.am(0, n[t], e, t, 0, i.t);
    e.s = 0;
    e.clamp();
    if (this.s != r.s) BigInteger.ZERO.subTo(e, e)
}
function bnpSquareTo(i) {
    var e = this.abs();
    var t = i.t = 2 * e.t;
    for (; --t >= 0;) i[t] = 0;
    for (t = 0; t < e.t - 1; ++t) {
        var n = e.am(t, e[t], i, 2 * t, 0, 1);
        if ((i[t + e.t] += e.am(t + 1, 2 * e[t], i, 2 * t + 1, n, e.t - t - 1)) >= e.DV) {
            i[t + e.t] -= e.DV;
            i[t + e.t + 1] = 1
        }
    }
    if (i.t > 0) i[i.t - 1] += e.am(t, e[t], i, 2 * t, 0, 1);
    i.s = 0;
    i.clamp()
}
function bnpDivRemTo(d, r, t) {
    var s = d.abs();
    if (! (s.t <= 0)) {
        var h = this.abs();
        if (! (h.t < s.t)) {
            if (null == t) t = nbi();
            var e = nbi(),
                f = this.s,
                g = d.s;
            var o = this.DB - nbits(s[s.t - 1]);
            if (o > 0) {
                s.lShiftTo(o, e);
                h.lShiftTo(o, t)
            } else {
                s.copyTo(e);
                h.copyTo(t)
            }
            var i = e.t;
            var u = e[i - 1];
            if (0 != u) {
                var l = u * (1 << this.F1) + (i > 1 ? e[i - 2] >> this.F2: 0);
                var p = this.FV / l,
                    m = (1 << this.F1) / l,
                    v = 1 << this.F2;
                var a = t.t,
                    _ = a - i,
                    n = null == r ? nbi() : r;
                e.dlShiftTo(_, n);
                if (t.compareTo(n) >= 0) {
                    t[t.t++] = 1;
                    t.subTo(n, t)
                }
                BigInteger.ONE.dlShiftTo(i, n);
                n.subTo(e, e);
                for (; e.t < i;) e[e.t++] = 0;
                for (; --_ >= 0;) {
                    var c = t[--a] == u ? this.DM: Math.floor(t[a] * p + (t[a - 1] + v) * m);
                    if ((t[a] += e.am(0, c, t, _, 0, i)) < c) {
                        e.dlShiftTo(_, n);
                        t.subTo(n, t);
                        for (; t[a] < --c;) t.subTo(n, t)
                    }
                }
                if (null != r) {
                    t.drShiftTo(i, r);
                    if (f != g) BigInteger.ZERO.subTo(r, r)
                }
                t.t = i;
                t.clamp();
                if (o > 0) t.rShiftTo(o, t);
                if (0 > f) BigInteger.ZERO.subTo(t, t)
            }
        } else {
            if (null != r) r.fromInt(0);
            if (null != t) this.copyTo(t)
        }
    }
}
function bnMod(e) {
    var t = nbi();
    this.abs().divRemTo(e, null, t);
    if (this.s < 0 && t.compareTo(BigInteger.ZERO) > 0) e.subTo(t, t);
    return t
}
function Classic(t) {
    this.m = t
}
function cConvert(t) {
    if (t.s < 0 || t.compareTo(this.m) >= 0) return t.mod(this.m);
    else return t
}
function cRevert(t) {
    return t
}
function cReduce(t) {
    t.divRemTo(this.m, null, t)
}
function cMulTo(e, i, t) {
    e.multiplyTo(i, t);
    this.reduce(t)
}
function cSqrTo(e, t) {
    e.squareTo(t);
    this.reduce(t)
}
Classic.prototype.convert = cConvert;
Classic.prototype.revert = cRevert;
Classic.prototype.reduce = cReduce;
Classic.prototype.mulTo = cMulTo;
Classic.prototype.sqrTo = cSqrTo;
function bnpInvDigit() {
    if (this.t < 1) return 0;
    var e = this[0];
    if (0 == (1 & e)) return 0;
    var t = 3 & e;
    t = t * (2 - (15 & e) * t) & 15;
    t = t * (2 - (255 & e) * t) & 255;
    t = t * (2 - ((65535 & e) * t & 65535)) & 65535;
    t = t * (2 - e * t % this.DV) % this.DV;
    return t > 0 ? this.DV - t: -t
}
function Montgomery(t) {
    this.m = t;
    this.mp = t.invDigit();
    this.mpl = 32767 & this.mp;
    this.mph = this.mp >> 15;
    this.um = (1 << t.DB - 15) - 1;
    this.mt2 = 2 * t.t
}
function montConvert(e) {
    var t = nbi();
    e.abs().dlShiftTo(this.m.t, t);
    t.divRemTo(this.m, null, t);
    if (e.s < 0 && t.compareTo(BigInteger.ZERO) > 0) this.m.subTo(t, t);
    return t
}
function montRevert(e) {
    var t = nbi();
    e.copyTo(t);
    this.reduce(t);
    return t
}
function montReduce(t) {
    for (; t.t <= this.mt2;) t[t.t++] = 0;
    for (var i = 0; i < this.m.t; ++i) {
        var e = 32767 & t[i];
        var n = e * this.mpl + ((e * this.mph + (t[i] >> 15) * this.mpl & this.um) << 15) & t.DM;
        e = i + this.m.t;
        t[e] += this.m.am(0, n, t, i, 0, this.m.t);
        for (; t[e] >= t.DV;) {
            t[e] -= t.DV;
            t[++e]++
        }
    }
    t.clamp();
    t.drShiftTo(this.m.t, t);
    if (t.compareTo(this.m) >= 0) t.subTo(this.m, t)
}
function montSqrTo(e, t) {
    e.squareTo(t);
    this.reduce(t)
}
function montMulTo(e, i, t) {
    e.multiplyTo(i, t);
    this.reduce(t)
}
Montgomery.prototype.convert = montConvert;
Montgomery.prototype.revert = montRevert;
Montgomery.prototype.reduce = montReduce;
Montgomery.prototype.mulTo = montMulTo;
Montgomery.prototype.sqrTo = montSqrTo;
function bnpIsEven() {
    return 0 == (this.t > 0 ? 1 & this[0] : this.s)
}
function bnpExp(e, i) {
    if (e > 4294967295 || 1 > e) return BigInteger.ONE;
    var t = nbi(),
        n = nbi(),
        r = i.convert(this),
        s = nbits(e) - 1;
    r.copyTo(t);
    for (; --s >= 0;) {
        i.sqrTo(t, n);
        if ((e & 1 << s) > 0) i.mulTo(n, r, t);
        else {
            var a = t;
            t = n;
            n = a
        }
    }
    return i.revert(t)
}
function bnModPowInt(i, t) {
    var e;
    if (256 > i || t.isEven()) e = new Classic(t);
    else e = new Montgomery(t);
    return this.exp(i, e)
}
BigInteger.prototype.copyTo = bnpCopyTo;
BigInteger.prototype.fromInt = bnpFromInt;
BigInteger.prototype.fromString = bnpFromString;
BigInteger.prototype.clamp = bnpClamp;
BigInteger.prototype.dlShiftTo = bnpDLShiftTo;
BigInteger.prototype.drShiftTo = bnpDRShiftTo;
BigInteger.prototype.lShiftTo = bnpLShiftTo;
BigInteger.prototype.rShiftTo = bnpRShiftTo;
BigInteger.prototype.subTo = bnpSubTo;
BigInteger.prototype.multiplyTo = bnpMultiplyTo;
BigInteger.prototype.squareTo = bnpSquareTo;
BigInteger.prototype.divRemTo = bnpDivRemTo;
BigInteger.prototype.invDigit = bnpInvDigit;
BigInteger.prototype.isEven = bnpIsEven;
BigInteger.prototype.exp = bnpExp;
BigInteger.prototype.toString = bnToString;
BigInteger.prototype.negate = bnNegate;
BigInteger.prototype.abs = bnAbs;
BigInteger.prototype.compareTo = bnCompareTo;
BigInteger.prototype.bitLength = bnBitLength;
BigInteger.prototype.mod = bnMod;
BigInteger.prototype.modPowInt = bnModPowInt;
BigInteger.ZERO = nbv(0);
BigInteger.ONE = nbv(1);
function bnClone() {
    var t = nbi();
    this.copyTo(t);
    return t
}
function bnIntValue() {
    if (this.s < 0) {
        if (1 == this.t) return this[0] - this.DV;
        else if (0 == this.t) return - 1
    } else if (1 == this.t) return this[0];
    else if (0 == this.t) return 0;
    return (this[1] & (1 << 32 - this.DB) - 1) << this.DB | this[0]
}
function bnByteValue() {
    return 0 == this.t ? this.s: this[0] << 24 >> 24
}
function bnShortValue() {
    return 0 == this.t ? this.s: this[0] << 16 >> 16
}
function bnpChunkSize(t) {
    return Math.floor(Math.LN2 * this.DB / Math.log(t))
}
function bnSigNum() {
    if (this.s < 0) return - 1;
    else if (this.t <= 0 || 1 == this.t && this[0] <= 0) return 0;
    else return 1
}
function bnpToRadix(t) {
    if (null == t) t = 10;
    if (0 == this.signum() || 2 > t || t > 36) return "0";
    var a = this.chunkSize(t);
    var r = Math.pow(t, a);
    var s = nbv(r),
        e = nbi(),
        i = nbi(),
        n = "";
    this.divRemTo(s, e, i);
    for (; e.signum() > 0;) {
        n = (r + i.intValue()).toString(t).substr(1) + n;
        e.divRemTo(s, e, i)
    }
    return i.intValue().toString(t) + n
}
function bnpFromRadix(r, t) {
    this.fromInt(0);
    if (null == t) t = 10;
    var s = this.chunkSize(t);
    var _ = Math.pow(t, s),
        a = !1,
        i = 0,
        e = 0;
    for (var n = 0; n < r.length; ++n) {
        var o = intAt(r, n);
        if (! (0 > o)) {
            e = t * e + o;
            if (++i >= s) {
                this.dMultiply(_);
                this.dAddOffset(e, 0);
                i = 0;
                e = 0
            }
        } else if ("-" == r.charAt(n) && 0 == this.signum()) a = !0
    }
    if (i > 0) {
        this.dMultiply(Math.pow(t, i));
        this.dAddOffset(e, 0)
    }
    if (a) BigInteger.ZERO.subTo(this, this)
}
function bnpFromNumber(t, i, r) {
    if ("number" == typeof i) if (2 > t) this.fromInt(1);
    else {
        this.fromNumber(t, r);
        if (!this.testBit(t - 1)) this.bitwiseTo(BigInteger.ONE.shiftLeft(t - 1), op_or, this);
        if (this.isEven()) this.dAddOffset(1, 0);
        for (; ! this.isProbablePrime(i);) {
            this.dAddOffset(2, 0);
            if (this.bitLength() > t) this.subTo(BigInteger.ONE.shiftLeft(t - 1), this)
        }
    } else {
        var e = new Array,
            n = 7 & t;
        e.length = (t >> 3) + 1;
        i.nextBytes(e);
        if (n > 0) e[0] &= (1 << n) - 1;
        else e[0] = 0;
        this.fromString(e, 256)
    }
}
function bnToByteArray() {
    var i = this.t,
        r = new Array;
    r[0] = this.s;
    var t = this.DB - i * this.DB % 8,
        e, n = 0;
    if (i-->0) {
        if (t < this.DB && (e = this[i] >> t) != (this.s & this.DM) >> t) r[n++] = e | this.s << this.DB - t;
        for (; i >= 0;) {
            if (8 > t) {
                e = (this[i] & (1 << t) - 1) << 8 - t;
                e |= this[--i] >> (t += this.DB - 8)
            } else {
                e = this[i] >> (t -= 8) & 255;
                if (0 >= t) {
                    t += this.DB; --i
                }
            }
            if (0 != (128 & e)) e |= -256;
            if (0 == n && (128 & this.s) != (128 & e))++n;
            if (n > 0 || e != this.s) r[n++] = e
        }
    }
    return r
}
function bnEquals(t) {
    return 0 == this.compareTo(t)
}
function bnMin(t) {
    return this.compareTo(t) < 0 ? this: t
}
function bnMax(t) {
    return this.compareTo(t) > 0 ? this: t
}
function bnpBitwiseTo(e, n, i) {
    var t, r, s = Math.min(e.t, this.t);
    for (t = 0; s > t; ++t) i[t] = n(this[t], e[t]);
    if (e.t < this.t) {
        r = e.s & this.DM;
        for (t = s; t < this.t; ++t) i[t] = n(this[t], r);
        i.t = this.t
    } else {
        r = this.s & this.DM;
        for (t = s; t < e.t; ++t) i[t] = n(r, e[t]);
        i.t = e.t
    }
    i.s = n(this.s, e.s);
    i.clamp()
}
function op_and(t, e) {
    return t & e
}
function bnAnd(e) {
    var t = nbi();
    this.bitwiseTo(e, op_and, t);
    return t
}
function op_or(t, e) {
    return t | e
}
function bnOr(e) {
    var t = nbi();
    this.bitwiseTo(e, op_or, t);
    return t
}
function op_xor(t, e) {
    return t ^ e
}
function bnXor(e) {
    var t = nbi();
    this.bitwiseTo(e, op_xor, t);
    return t
}
function op_andnot(t, e) {
    return t & ~e
}
function bnAndNot(e) {
    var t = nbi();
    this.bitwiseTo(e, op_andnot, t);
    return t
}
function bnNot() {
    var t = nbi();
    for (var e = 0; e < this.t; ++e) t[e] = this.DM & ~this[e];
    t.t = this.t;
    t.s = ~this.s;
    return t
}
function bnShiftLeft(t) {
    var e = nbi();
    if (0 > t) this.rShiftTo( - t, e);
    else this.lShiftTo(t, e);
    return e
}
function bnShiftRight(t) {
    var e = nbi();
    if (0 > t) this.lShiftTo( - t, e);
    else this.rShiftTo(t, e);
    return e
}
function lbit(t) {
    if (0 == t) return - 1;
    var e = 0;
    if (0 == (65535 & t)) {
        t >>= 16;
        e += 16
    }
    if (0 == (255 & t)) {
        t >>= 8;
        e += 8
    }
    if (0 == (15 & t)) {
        t >>= 4;
        e += 4
    }
    if (0 == (3 & t)) {
        t >>= 2;
        e += 2
    }
    if (0 == (1 & t))++e;
    return e
}
function bnGetLowestSetBit() {
    for (var t = 0; t < this.t; ++t) if (0 != this[t]) return t * this.DB + lbit(this[t]);
    if (this.s < 0) return this.t * this.DB;
    else return - 1
}
function cbit(t) {
    var e = 0;
    for (; 0 != t;) {
        t &= t - 1; ++e
    }
    return e
}
function bnBitCount() {
    var e = 0,
        i = this.s & this.DM;
    for (var t = 0; t < this.t; ++t) e += cbit(this[t] ^ i);
    return e
}
function bnTestBit(t) {
    var e = Math.floor(t / this.DB);
    if (e >= this.t) return 0 != this.s;
    else return 0 != (this[e] & 1 << t % this.DB)
}
function bnpChangeBit(e, i) {
    var t = BigInteger.ONE.shiftLeft(e);
    this.bitwiseTo(t, i, t);
    return t
}
function bnSetBit(t) {
    return this.changeBit(t, op_or)
}
function bnClearBit(t) {
    return this.changeBit(t, op_andnot)
}
function bnFlipBit(t) {
    return this.changeBit(t, op_xor)
}
function bnpAddTo(n, i) {
    var e = 0,
        t = 0,
        r = Math.min(n.t, this.t);
    for (; r > e;) {
        t += this[e] + n[e];
        i[e++] = t & this.DM;
        t >>= this.DB
    }
    if (n.t < this.t) {
        t += n.s;
        for (; e < this.t;) {
            t += this[e];
            i[e++] = t & this.DM;
            t >>= this.DB
        }
        t += this.s
    } else {
        t += this.s;
        for (; e < n.t;) {
            t += n[e];
            i[e++] = t & this.DM;
            t >>= this.DB
        }
        t += n.s
    }
    i.s = 0 > t ? -1 : 0;
    if (t > 0) i[e++] = t;
    else if ( - 1 > t) i[e++] = this.DV + t;
    i.t = e;
    i.clamp()
}
function bnAdd(e) {
    var t = nbi();
    this.addTo(e, t);
    return t
}
function bnSubtract(e) {
    var t = nbi();
    this.subTo(e, t);
    return t
}
function bnMultiply(e) {
    var t = nbi();
    this.multiplyTo(e, t);
    return t
}
function bnSquare() {
    var t = nbi();
    this.squareTo(t);
    return t
}
function bnDivide(e) {
    var t = nbi();
    this.divRemTo(e, t, null);
    return t
}
function bnRemainder(e) {
    var t = nbi();
    this.divRemTo(e, null, t);
    return t
}
function bnDivideAndRemainder(i) {
    var t = nbi(),
        e = nbi();
    this.divRemTo(i, t, e);
    return new Array(t, e)
}
function bnpDMultiply(t) {
    this[this.t] = this.am(0, t - 1, this, 0, 0, this.t); ++this.t;
    this.clamp()
}
function bnpDAddOffset(e, t) {
    if (0 != e) {
        for (; this.t <= t;) this[this.t++] = 0;
        this[t] += e;
        for (; this[t] >= this.DV;) {
            this[t] -= this.DV;
            if (++t >= this.t) this[this.t++] = 0; ++this[t]
        }
    }
}
function NullExp() {}
function nNop(t) {
    return t
}
function nMulTo(t, e, i) {
    t.multiplyTo(e, i)
}
function nSqrTo(t, e) {
    t.squareTo(e)
}
NullExp.prototype.convert = nNop;
NullExp.prototype.revert = nNop;
NullExp.prototype.mulTo = nMulTo;
NullExp.prototype.sqrTo = nSqrTo;
function bnPow(t) {
    return this.exp(t, new NullExp)
}
function bnpMultiplyLowerTo(i, r, e) {
    var t = Math.min(this.t + i.t, r);
    e.s = 0;
    e.t = t;
    for (; t > 0;) e[--t] = 0;
    var n;
    for (n = e.t - this.t; n > t; ++t) e[t + this.t] = this.am(0, i[t], e, t, 0, this.t);
    for (n = Math.min(i.t, r); n > t; ++t) this.am(0, i[t], e, t, 0, r - t);
    e.clamp()
}
function bnpMultiplyUpperTo(n, i, e) {--i;
    var t = e.t = this.t + n.t - i;
    e.s = 0;
    for (; --t >= 0;) e[t] = 0;
    for (t = Math.max(i - this.t, 0); t < n.t; ++t) e[this.t + t - i] = this.am(i - t, n[t], e, 0, 0, this.t + t - i);
    e.clamp();
    e.drShiftTo(1, e)
}
function Barrett(t) {
    this.r2 = nbi();
    this.q3 = nbi();
    BigInteger.ONE.dlShiftTo(2 * t.t, this.r2);
    this.mu = this.r2.divide(t);
    this.m = t
}
function barrettConvert(t) {
    if (t.s < 0 || t.t > 2 * this.m.t) return t.mod(this.m);
    else if (t.compareTo(this.m) < 0) return t;
    else {
        var e = nbi();
        t.copyTo(e);
        this.reduce(e);
        return e
    }
}
function barrettRevert(t) {
    return t
}
function barrettReduce(t) {
    t.drShiftTo(this.m.t - 1, this.r2);
    if (t.t > this.m.t + 1) {
        t.t = this.m.t + 1;
        t.clamp()
    }
    this.mu.multiplyUpperTo(this.r2, this.m.t + 1, this.q3);
    this.m.multiplyLowerTo(this.q3, this.m.t + 1, this.r2);
    for (; t.compareTo(this.r2) < 0;) t.dAddOffset(1, this.m.t + 1);
    t.subTo(this.r2, t);
    for (; t.compareTo(this.m) >= 0;) t.subTo(this.m, t)
}
function barrettSqrTo(e, t) {
    e.squareTo(t);
    this.reduce(t)
}
function barrettMulTo(e, i, t) {
    e.multiplyTo(i, t);
    this.reduce(t)
}
Barrett.prototype.convert = barrettConvert;
Barrett.prototype.revert = barrettRevert;
Barrett.prototype.reduce = barrettReduce;
Barrett.prototype.mulTo = barrettMulTo;
Barrett.prototype.sqrTo = barrettSqrTo;
function bnModPow(o, u) {
    var t = o.bitLength(),
        s,
        e = nbv(1),
        i;
    if (0 >= t) return e;
    else if (18 > t) s = 1;
    else if (48 > t) s = 3;
    else if (144 > t) s = 4;
    else if (768 > t) s = 5;
    else s = 6;
    if (8 > t) i = new Classic(u);
    else if (u.isEven()) i = new Barrett(u);
    else i = new Montgomery(u);
    var _ = new Array,
        n = 3,
        l = s - 1,
        p = (1 << s) - 1;
    _[1] = i.convert(this);
    if (s > 1) {
        var f = nbi();
        i.sqrTo(_[1], f);
        for (; p >= n;) {
            _[n] = nbi();
            i.mulTo(f, _[n - 2], _[n]);
            n += 2
        }
    }
    var r = o.t - 1,
        c, d = !0,
        a = nbi(),
        h;
    t = nbits(o[r]) - 1;
    for (; r >= 0;) {
        if (t >= l) c = o[r] >> t - l & p;
        else {
            c = (o[r] & (1 << t + 1) - 1) << l - t;
            if (r > 0) c |= o[r - 1] >> this.DB + t - l
        }
        n = s;
        for (; 0 == (1 & c);) {
            c >>= 1; --n
        }
        if ((t -= n) < 0) {
            t += this.DB; --r
        }
        if (d) {
            _[c].copyTo(e);
            d = !1
        } else {
            for (; n > 1;) {
                i.sqrTo(e, a);
                i.sqrTo(a, e);
                n -= 2
            }
            if (n > 0) i.sqrTo(e, a);
            else {
                h = e;
                e = a;
                a = h
            }
            i.mulTo(a, _[c], e)
        }
        for (; r >= 0 && 0 == (o[r] & 1 << t);) {
            i.sqrTo(e, a);
            h = e;
            e = a;
            a = h;
            if (--t < 0) {
                t = this.DB - 1; --r
            }
        }
    }
    return i.revert(e)
}
function bnGCD(r) {
    var e = this.s < 0 ? this.negate() : this.clone();
    var t = r.s < 0 ? r.negate() : r.clone();
    if (e.compareTo(t) < 0) {
        var s = e;
        e = t;
        t = s
    }
    var n = e.getLowestSetBit(),
        i = t.getLowestSetBit();
    if (0 > i) return e;
    if (i > n) i = n;
    if (i > 0) {
        e.rShiftTo(i, e);
        t.rShiftTo(i, t)
    }
    for (; e.signum() > 0;) {
        if ((n = e.getLowestSetBit()) > 0) e.rShiftTo(n, e);
        if ((n = t.getLowestSetBit()) > 0) t.rShiftTo(n, t);
        if (e.compareTo(t) >= 0) {
            e.subTo(t, e);
            e.rShiftTo(1, e)
        } else {
            t.subTo(e, t);
            t.rShiftTo(1, t)
        }
    }
    if (i > 0) t.lShiftTo(i, t);
    return t
}
function bnpModInt(t) {
    if (0 >= t) return 0;
    var n = this.DV % t,
        e = this.s < 0 ? t - 1 : 0;
    if (this.t > 0) if (0 == n) e = this[0] % t;
    else for (var i = this.t - 1; i >= 0; --i) e = (n * e + this[i]) % t;
    return e
}
function bnModInverse(e) {
    var o = e.isEven();
    if (this.isEven() && o || 0 == e.signum()) return BigInteger.ZERO;
    var n = e.clone(),
        r = this.clone();
    var s = nbv(1),
        i = nbv(0),
        a = nbv(0),
        t = nbv(1);
    for (; 0 != n.signum();) {
        for (; n.isEven();) {
            n.rShiftTo(1, n);
            if (o) {
                if (!s.isEven() || !i.isEven()) {
                    s.addTo(this, s);
                    i.subTo(e, i)
                }
                s.rShiftTo(1, s)
            } else if (!i.isEven()) i.subTo(e, i);
            i.rShiftTo(1, i)
        }
        for (; r.isEven();) {
            r.rShiftTo(1, r);
            if (o) {
                if (!a.isEven() || !t.isEven()) {
                    a.addTo(this, a);
                    t.subTo(e, t)
                }
                a.rShiftTo(1, a)
            } else if (!t.isEven()) t.subTo(e, t);
            t.rShiftTo(1, t)
        }
        if (n.compareTo(r) >= 0) {
            n.subTo(r, n);
            if (o) s.subTo(a, s);
            i.subTo(t, i)
        } else {
            r.subTo(n, r);
            if (o) a.subTo(s, a);
            t.subTo(i, t)
        }
    }
    if (0 != r.compareTo(BigInteger.ONE)) return BigInteger.ZERO;
    if (t.compareTo(e) >= 0) return t.subtract(e);
    if (t.signum() < 0) t.addTo(e, t);
    else return t;
    if (t.signum() < 0) return t.add(e);
    else return t
}
var lowprimes = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997];
var lplim = (1 << 26) / lowprimes[lowprimes.length - 1];
function bnIsProbablePrime(r) {
    var t, e = this.abs();
    if (1 == e.t && e[0] <= lowprimes[lowprimes.length - 1]) {
        for (t = 0; t < lowprimes.length; ++t) if (e[0] == lowprimes[t]) return ! 0;
        return ! 1
    }
    if (e.isEven()) return ! 1;
    t = 1;
    for (; t < lowprimes.length;) {
        var i = lowprimes[t],
            n = t + 1;
        for (; n < lowprimes.length && lplim > i;) i *= lowprimes[n++];
        i = e.modInt(i);
        for (; n > t;) if (i % lowprimes[t++] == 0) return ! 1
    }
    return e.millerRabin(r)
}
function bnpMillerRabin(e) {
    var i = this.subtract(BigInteger.ONE);
    var n = i.getLowestSetBit();
    if (0 >= n) return ! 1;
    var a = i.shiftRight(n);
    e = e + 1 >> 1;
    if (e > lowprimes.length) e = lowprimes.length;
    var r = nbi();
    for (var s = 0; e > s; ++s) {
        r.fromInt(lowprimes[Math.floor(Math.random() * lowprimes.length)]);
        var t = r.modPow(a, this);
        if (0 != t.compareTo(BigInteger.ONE) && 0 != t.compareTo(i)) {
            var o = 1;
            for (; o++<n && 0 != t.compareTo(i);) {
                t = t.modPowInt(2, this);
                if (0 == t.compareTo(BigInteger.ONE)) return ! 1
            }
            if (0 != t.compareTo(i)) return ! 1
        }
    }
    return ! 0
}
BigInteger.prototype.chunkSize = bnpChunkSize;
BigInteger.prototype.toRadix = bnpToRadix;
BigInteger.prototype.fromRadix = bnpFromRadix;
BigInteger.prototype.fromNumber = bnpFromNumber;
BigInteger.prototype.bitwiseTo = bnpBitwiseTo;
BigInteger.prototype.changeBit = bnpChangeBit;
BigInteger.prototype.addTo = bnpAddTo;
BigInteger.prototype.dMultiply = bnpDMultiply;
BigInteger.prototype.dAddOffset = bnpDAddOffset;
BigInteger.prototype.multiplyLowerTo = bnpMultiplyLowerTo;
BigInteger.prototype.multiplyUpperTo = bnpMultiplyUpperTo;
BigInteger.prototype.modInt = bnpModInt;
BigInteger.prototype.millerRabin = bnpMillerRabin;
BigInteger.prototype.clone = bnClone;
BigInteger.prototype.intValue = bnIntValue;
BigInteger.prototype.byteValue = bnByteValue;
BigInteger.prototype.shortValue = bnShortValue;
BigInteger.prototype.signum = bnSigNum;
BigInteger.prototype.toByteArray = bnToByteArray;
BigInteger.prototype.equals = bnEquals;
BigInteger.prototype.min = bnMin;
BigInteger.prototype.max = bnMax;
BigInteger.prototype.and = bnAnd;
BigInteger.prototype.or = bnOr;
BigInteger.prototype.xor = bnXor;
BigInteger.prototype.andNot = bnAndNot;
BigInteger.prototype.not = bnNot;
BigInteger.prototype.shiftLeft = bnShiftLeft;
BigInteger.prototype.shiftRight = bnShiftRight;
BigInteger.prototype.getLowestSetBit = bnGetLowestSetBit;
BigInteger.prototype.bitCount = bnBitCount;
BigInteger.prototype.testBit = bnTestBit;
BigInteger.prototype.setBit = bnSetBit;
BigInteger.prototype.clearBit = bnClearBit;
BigInteger.prototype.flipBit = bnFlipBit;
BigInteger.prototype.add = bnAdd;
BigInteger.prototype.subtract = bnSubtract;
BigInteger.prototype.multiply = bnMultiply;
BigInteger.prototype.divide = bnDivide;
BigInteger.prototype.remainder = bnRemainder;
BigInteger.prototype.divideAndRemainder = bnDivideAndRemainder;
BigInteger.prototype.modPow = bnModPow;
BigInteger.prototype.modInverse = bnModInverse;
BigInteger.prototype.pow = bnPow;
BigInteger.prototype.gcd = bnGCD;
BigInteger.prototype.isProbablePrime = bnIsProbablePrime;
BigInteger.prototype.square = bnSquare;
if ("object" != typeof JSON) JSON = {}; !
    function() {
        "use strict";
        function f(t) {
            return 10 > t ? "0" + t: t
        }
        function quote(t) {
            escapable.lastIndex = 0;
            return escapable.test(t) ? '"' + t.replace(escapable,
                function(t) {
                    var e = meta[t];
                    return "string" == typeof e ? e: "\\u" + ("0000" + t.charCodeAt(0).toString(16)).slice( - 4)
                }) + '"': '"' + t + '"'
        }
        function str(o, _) {
            var e, r, i, s, a = gap,
                n, t = _[o];
            if (t && "object" == typeof t && "function" == typeof t.toJSON) t = t.toJSON(o);
            if ("function" == typeof rep) t = rep.call(_, o, t);
            switch (typeof t) {
                case "string":
                    return quote(t);
                case "number":
                    return isFinite(t) ? String(t) : "null";
                case "boolean":
                case "null":
                    return String(t);
                case "object":
                    if (!t) return "null";
                    gap += indent;
                    n = [];
                    if ("[object Array]" === Object.prototype.toString.apply(t)) {
                        s = t.length;
                        for (e = 0; s > e; e += 1) n[e] = str(e, t) || "null";
                        i = 0 === n.length ? "[]": gap ? "[\n" + gap + n.join(",\n" + gap) + "\n" + a + "]": "[" + n.join(",") + "]";
                        gap = a;
                        return i
                    }
                    if (rep && "object" == typeof rep) {
                        s = rep.length;
                        for (e = 0; s > e; e += 1) if ("string" == typeof rep[e]) {
                            r = rep[e];
                            i = str(r, t);
                            if (i) n.push(quote(r) + (gap ? ": ": ":") + i)
                        }
                    } else for (r in t) if (Object.prototype.hasOwnProperty.call(t, r)) {
                        i = str(r, t);
                        if (i) n.push(quote(r) + (gap ? ": ": ":") + i)
                    }
                    i = 0 === n.length ? "{}": gap ? "{\n" + gap + n.join(",\n" + gap) + "\n" + a + "}": "{" + n.join(",") + "}";
                    gap = a;
                    return i
            }
        }
        if ("function" != typeof Date.prototype.toJSON) {
            Date.prototype.toJSON = function() {
                return isFinite(this.valueOf()) ? this.getUTCFullYear() + "-" + f(this.getUTCMonth() + 1) + "-" + f(this.getUTCDate()) + "T" + f(this.getUTCHours()) + ":" + f(this.getUTCMinutes()) + ":" + f(this.getUTCSeconds()) + "Z": null
            };
            String.prototype.toJSON = Number.prototype.toJSON = Boolean.prototype.toJSON = function() {
                return this.valueOf()
            }
        }
        var cx, escapable, gap, indent, meta, rep;
        if ("function" != typeof JSON.stringify) {
            escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
            meta = {
                "\b": "\\b",
                "	": "\\t",
                "\n": "\\n",
                "\f": "\\f",
                "\r": "\\r",
                '"': '\\"',
                "\\": "\\\\"
            };
            JSON.stringify = function(n, t, e) {
                var i;
                gap = "";
                indent = "";
                if ("number" == typeof e) for (i = 0; e > i; i += 1) indent += " ";
                else if ("string" == typeof e) indent = e;
                rep = t;
                if (t && "function" != typeof t && ("object" != typeof t || "number" != typeof t.length)) throw new Error("JSON.stringify");
                return str("", {
                    "": n
                })
            }
        }
        if ("function" != typeof JSON.parse) {
            cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
            JSON.parse = function(text, reviver) {
                function walk(n, r) {
                    var e, i, t = n[r];
                    if (t && "object" == typeof t) for (e in t) if (Object.prototype.hasOwnProperty.call(t, e)) {
                        i = walk(t, e);
                        if (void 0 !== i) t[e] = i;
                        else delete t[e]
                    }
                    return reviver.call(n, r, t)
                }
                var j;
                text = String(text);
                cx.lastIndex = 0;
                if (cx.test(text)) text = text.replace(cx,
                    function(t) {
                        return "\\u" + ("0000" + t.charCodeAt(0).toString(16)).slice( - 4)
                    });
                if (/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, "@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, "]").replace(/(?:^|:|,)(?:\s*\[)+/g, ""))) {
                    j = eval("(" + text + ")");
                    return "function" == typeof reviver ? walk({
                            "": j
                        },
                        "") : j
                }
                throw new SyntaxError("JSON.parse")
            }
        }
    } ();
var RSAPublicKey = function(t, e) {
    this.modulus = new BigInteger(Hex.encode(t), 16);
    this.encryptionExponent = new BigInteger(Hex.encode(e), 16)
};
var UTF8 = {
    encode: function(i) {
        i = i.replace(/\r\n/g, "\n");
        var e = "";
        for (var n = 0; n < i.length; n++) {
            var t = i.charCodeAt(n);
            if (128 > t) e += String.fromCharCode(t);
            else if (t > 127 && 2048 > t) {
                e += String.fromCharCode(t >> 6 | 192);
                e += String.fromCharCode(63 & t | 128)
            } else {
                e += String.fromCharCode(t >> 12 | 224);
                e += String.fromCharCode(t >> 6 & 63 | 128);
                e += String.fromCharCode(63 & t | 128)
            }
        }
        return e
    },
    decode: function(i) {
        var n = "";
        var t = 0;
        var e = $c1 = $c2 = 0;
        for (; t < i.length;) {
            e = i.charCodeAt(t);
            if (128 > e) {
                n += String.fromCharCode(e);
                t++
            } else if (e > 191 && 224 > e) {
                $c2 = i.charCodeAt(t + 1);
                n += String.fromCharCode((31 & e) << 6 | 63 & $c2);
                t += 2
            } else {
                $c2 = i.charCodeAt(t + 1);
                $c3 = i.charCodeAt(t + 2);
                n += String.fromCharCode((15 & e) << 12 | (63 & $c2) << 6 | 63 & $c3);
                t += 3
            }
        }
        return n
    }
};
var Base64 = {
    base64: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
    encode: function(t) {
        if (!t) return ! 1;
        var o = "";
        var s, e, i;
        var _, c, a, n;
        var r = 0;
        do {
            s = t.charCodeAt(r++);
            e = t.charCodeAt(r++);
            i = t.charCodeAt(r++);
            _ = s >> 2;
            c = (3 & s) << 4 | e >> 4;
            a = (15 & e) << 2 | i >> 6;
            n = 63 & i;
            if (isNaN(e)) a = n = 64;
            else if (isNaN(i)) n = 64;
            o += this.base64.charAt(_) + this.base64.charAt(c) + this.base64.charAt(a) + this.base64.charAt(n)
        } while ( r < t . length );
        return o
    },
    decode: function(t) {
        if (!t) return ! 1;
        t = t.replace(/[^A-Za-z0-9\+\/\=]/g, "");
        var i = "";
        var a, r, n, s;
        var e = 0;
        do {
            a = this.base64.indexOf(t.charAt(e++));
            r = this.base64.indexOf(t.charAt(e++));
            n = this.base64.indexOf(t.charAt(e++));
            s = this.base64.indexOf(t.charAt(e++));
            i += String.fromCharCode(a << 2 | r >> 4);
            if (64 != n) i += String.fromCharCode((15 & r) << 4 | n >> 2);
            if (64 != s) i += String.fromCharCode((3 & n) << 6 | s)
        } while ( e < t . length );
        return i
    }
};
var Hex = {
    hex: "0123456789abcdef",
    encode: function(t) {
        if (!t) return ! 1;
        var i = "";
        var e;
        var n = 0;
        do {
            e = t.charCodeAt(n++);
            i += this.hex.charAt(e >> 4 & 15) + this.hex.charAt(15 & e)
        } while ( n < t . length );
        return i
    },
    decode: function(t) {
        if (!t) return ! 1;
        t = t.replace(/[^0-9abcdef]/g, "");
        var i = "";
        var e = 0;
        do i += String.fromCharCode(this.hex.indexOf(t.charAt(e++)) << 4 & 240 | 15 & this.hex.indexOf(t.charAt(e++)));
        while (e < t.length);
        return i
    }
};
var ASN1Data = function(t) {
    this.error = !1;
    this.parse = function(t) {
        if (!t) {
            this.error = !0;
            return null
        }
        var r = [];
        for (; t.length > 0;) {
            var i = t.charCodeAt(0);
            t = t.substr(1);
            var e = 0;
            if (5 == (31 & i)) t = t.substr(1);
            else if (128 & t.charCodeAt(0)) {
                var n = 127 & t.charCodeAt(0);
                t = t.substr(1);
                if (n > 0) e = t.charCodeAt(0);
                if (n > 1) e = e << 8 | t.charCodeAt(1);
                if (n > 2) {
                    this.error = !0;
                    return null
                }
                t = t.substr(n)
            } else {
                e = t.charCodeAt(0);
                t = t.substr(1)
            }
            var s = "";
            if (e) {
                if (e > t.length) {
                    this.error = !0;
                    return null
                }
                s = t.substr(0, e);
                t = t.substr(e)
            }
            if (32 & i) r.push(this.parse(s));
            else r.push(this.value(128 & i ? 4 : 31 & i, s))
        }
        return r
    };
    this.value = function(e, t) {
        if (1 == e) return t ? !0 : !1;
        else if (2 == e) return t;
        else if (3 == e) return this.parse(t.substr(1));
        else if (5 == e) return null;
        else if (6 == e) {
            var i = [];
            var o = t.charCodeAt(0);
            i.push(Math.floor(o / 40));
            i.push(o - 40 * i[0]);
            var n = [];
            var a = 0;
            var r;
            for (r = 1; r < t.length; r++) {
                var _ = t.charCodeAt(r);
                n.push(127 & _);
                if (128 & _) a++;
                else {
                    var s;
                    var c = 0;
                    for (s = 0; s < n.length; s++) c += n[s] * Math.pow(128, a--);
                    i.push(c);
                    a = 0;
                    n = []
                }
            }
            return i.join(".")
        }
        return null
    };
    this.data = this.parse(t)
};
var RSA = {
    getPublicKey: function(t) {
        if (t.length < 50) return ! 1;
        if ("-----BEGIN PUBLIC KEY-----" != t.substr(0, 26)) return ! 1;
        t = t.substr(26);
        if ("-----END PUBLIC KEY-----" != t.substr(t.length - 24)) return ! 1;
        t = t.substr(0, t.length - 24);
        t = new ASN1Data(Base64.decode(t));
        if (t.error) return ! 1;
        t = t.data;
        if ("1.2.840.113549.1.1.1" == t[0][0][0]) return new RSAPublicKey(t[0][1][0][0], t[0][1][0][1]);
        else return ! 1
    },
    encrypt: function(t, e) {
        if (!e) return ! 1;
        var i = e.modulus.bitLength() + 7 >> 3;
        t = this.pkcs1pad2(t, i);
        if (!t) return ! 1;
        t = t.modPowInt(e.encryptionExponent, e.modulus);
        if (!t) return ! 1;
        t = t.toString(16);
        for (; t.length < 2 * i;) t = "0" + t;
        return Base64.encode(Hex.decode(t))
    },
    decrypt: function(t) {
        var e = new BigInteger(t, 16)
    },
    pkcs1pad2: function(i, t) {
        if (t < i.length + 11) return null;
        var e = [];
        var n = i.length - 1;
        for (; n >= 0 && t > 0;) e[--t] = i.charCodeAt(n--);
        e[--t] = 0;
        for (; t > 2;) e[--t] = Math.floor(254 * Math.random()) + 1;
        e[--t] = 2;
        e[--t] = 0;
        return new BigInteger(e)
    }
};
var MpUtil = function() {
    var t = function(t, e, i) {
        t.addEventListener ? t.addEventListener(e, i, !1) : t.attachEvent("on" + e, i)
    };
    var e = function(t, e, i) {
        t.removeEventListener ? t.removeEventListener(e, i, !1) : t.detachEvent("on" + e, i)
    };
    var i = function() {
        var t = +new Date;
        return function() {
            return "" + t++
        }
    } ();
    var n = function(e, t) {
        try {
            t = t.toLowerCase();
            if (null === e) return "null" == t;
            if (void 0 === e) return "undefined" == t;
            else return Object.prototype.toString.call(e).toLowerCase() == "[object " + t + "]"
        } catch(i) {
            return ! 1
        }
    };
    return {
        addEvent: t,
        clearEvent: e,
        uniqueId: i,
        isTypeOf: n
    }
} ();
var MP = function() {
    var e = "zc.reg.163.com",
        r = "ntes_zc_",
        i = "-----BEGIN PUBLIC KEY-----MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC5gsH+AA4XWONB5TDcUd+xCz7ejOFHZKlcZDx+pF1i7Gsvi1vjyJoQhRtRSn950x498VUkx7rUxg1/ScBVfrRxQOZ8xFBye3pjAzfb22+RCuYApSVpJ3OO3KsEuKExftz9oFBv3ejxPlYc5yq7YiBO8XlTnQN0Sa4R4qhPO3I2MQIDAQAB-----END PUBLIC KEY-----",
        n = "dl.reg.163.com",
        t = function(t) {
            var i = t.data,
                n = (t.host ? t.host: e) + t.path;
            var a;
            if ("string" == typeof i) i = JSON.parse(i);
            a = i;
            var _ = a.isleak ? 1 : 0;
            delete i.isleak;
            delete a.isleak;
            if ("POST" == t.type) i = JSON.stringify(i);
            var r = -1 == n.indexOf("zc.reg.163.com");
            if (r) r = -1 == n.indexOf("/zc/");
            if (!r) n = window.REGPROTOCOL + n;
            else n = window.PROTOCOL + n;
            var o = r ? {
                url: n,
                type: t.type,
                data: i,
                contentType: t.contentType || "application/json",
                dataType: t.dataType || "json",
                timeout: 1e4,
                success: function(e) {
                    var i = e && e.ret;
                    if (201 != i) t.error(t.path, e);
                    else t.success(t.path, e)
                },
                error: function() {
                    var e = Array.prototype.slice.call(arguments);
                    e.unshift(t.path);
                    t.error.apply(null, e)
                }
            }: {
                url: n,
                type: t.type,
                data: i,
                contentType: t.contentType || "application/json",
                dataType: t.dataType || "json",
                timeout: 1e4,
                success: function(e) {
                    if (e && e.ret && ("102" === e.ret || "104" === e.ret || "200" === e.ret || "201" === e.ret || "202" === e.ret)) t.success(t.path, e);
                    else t.error(t.path, e)
                },
                error: function() {
                    var e = Array.prototype.slice.call(arguments);
                    e.unshift(t.path);
                    t.error.apply(null, e)
                }
            };
            s(o)
        };
    var s = function() {
        var e = function(t) {
            var e = [];
            for (var i in t) e.push(encodeURIComponent(i) + "=" + encodeURIComponent(t[i]));
            return e.join("&")
        };
        var i = function() {
            if ("undefined" != typeof XMLHttpRequest) return new XMLHttpRequest;
            else if ("undefined" != typeof ActiveXObject) {
                var e = ["MSXML2.XMLHttp.6.0", "MSXML2.XMLHttp.3.0", "MSXML2.XMLHttp"];
                for (var t = 0; t < e.length; t++) try {
                    return new ActiveXObject(e[t])
                } catch(i) {}
            } else throw new Error("您的系统或浏览器不支持XHR对象！")
        };
        var n = function(e) {
            var t = [];
            for (var i in e) t.push(i + "=" + e[i]);
            t.push("nocache=" + (new Date).getTime());
            return t.join("&")
        };
        var r = function() {
            var t = function(t) {
                try {
                    return new Function("return " + t)()
                } catch(e) {
                    return null
                }
            };
            return function(e) {
                if ("string" != typeof e) return e;
                try {
                    if (window.JSON && JSON.parse) return JSON.parse(e)
                } catch(i) {}
                return t(e)
            }
        } ();
        var t = function(e, t) {
            if (4 == e.readyState && !t.requestDone) {
                var n = e.status;
                var i = r(e.responseText) || {};
                if (n >= 200 && 300 > n) t.success && t.success(i);
                else {
                    i.eurl = t.url;
                    t.error && t.error(i)
                }
                this.xhr = null;
                clearTimeout(t.reqTimeout)
            } else if (!t.requestDone) if (!t.reqTimeout) t.reqTimeout = setTimeout(function() {
                    t.requestDone = !0; !! this.xhr && this.xhr.abort();
                    t.error && t.error({
                        ret: "-1"
                    });
                    clearTimeout(t.reqTimeout)
                },
                !t.timeout ? 5e3: t.timeout)
        };
        return function(r) {
            r = r || {};
            r.requestDone = !1;
            r.type = (r.type || "GET").toUpperCase();
            r.dataType = r.dataType || "json";
            r.contentType = r.contentType || "application/x-www-form-urlencoded";
            r.async = r.async || !0;
            var s = r.data;
            var a = i();
            if (r.async === !0) a.onreadystatechange = function() {
                t(a, r)
            };
            if ("GET" == r.type) {
                s = n(s);
                a.open("GET", r.url + "?" + s, r.async);
                a.send(null)
            } else if ("POST" == r.type) {
                a.open("POST", r.url, r.async);
                a.setRequestHeader("Content-Type", r.contentType);
                if ("application/x-www-form-urlencoded" == r.contentType) {
                    try {
                        s = JSON.parse(s)
                    } catch(o) {}
                    s = e(s)
                }
                a.send(s)
            }
            if (r.async === !1) t(a, r)
        }
    } ();
    return {
        promarkIdData: {},
        TICKET: "",
        encrypt: function(t, e) {
            var n = RSA.getPublicKey(i);
            return RSA.encrypt(t + "`" + e, n)
        },
        encrypt2: function(t) {
            var e = RSA.getPublicKey(i);
            return RSA.encrypt(t, e)
        },
        getId: function(t, e) {
            MpRequest.getCookie(r + t, e)
        },
        regvftcp: function(e, i, n, r) {
            t({
                path: "/vftcp",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        init: function(e, i, n, r) {
            t({
                path: "/ini",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        getCaptcha: function(n) {
            var i = e,
                t = window["$regCookieDomain"];
            if (t) if (t.indexOf("icourse163.org") >= 0) i = "reg." + t + "/zc";
            else i = "passport." + t + "/zc";
            return window.REGPROTOCOL + i + "/cp?channel=2&id=" + n + "&nocache=" + MpUtil.uniqueId()
        },
        checkCaptcha: function(e, i, n, r) {
            t({
                path: "/vfcp",
                type: "POST",
                contentType: "application/x-www-form-urlencoded",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        initQrcode: function(e, i, n) {
            t({
                host: "reg.163.com",
                path: "/services/getqrcodeid",
                type: "GET",
                data: e,
                success: i,
                error: n
            })
        },
        checkName: function(e, i, n, r) {
            t({
                path: "/chn",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        getMobileSms: function(e, i, n, r) {
            t({
                path: "/sm",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        getMailSms: function(e, i, n, r) {
            t({
                path: "/mlrgsm",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        getTicket: function(e, i, n, r) {
            t({
                path: "/gt",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(e),
                success: i,
                error: n,
                host: r
            })
        },
        setTicket: function(t) {
            MP.TICKET = t || ""
        },
        regMob: function(e, i, n, r) {
            t({
                path: "/mrg",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(e),
                success: i,
                error: n,
                host: r
            })
        },
        fastReg: function(e, i, n, r) {
            t({
                path: "/frg",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(e),
                success: i,
                error: n,
                host: r
            })
        },
        sendActMail: function(e, i, n, r) {
            t({
                path: "/sendActMail",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        qrlogin: function(e, i, n, r) {
            t({
                path: "/qrcodel",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        getCaptchaLogin: function(i, r, e) {
            var t = n;
            if ("mail126" === i) t = "passport.126.com/dl";
            else if ("mailyeah" === i) t = "passport.yeah.net/dl";
            if (e) if (e.indexOf("icourse163.org") >= 0) t = "reg." + e + "/dl";
            else t = "passport." + e + "/dl";
            return window.PROTOCOL + t + "/cp?pd=" + i + "&pkid=" + r + "&random=" + MpUtil.uniqueId()
        },
        safelogin: function(e, i, n, r) {
            t({
                path: "/l",
                type: "POST",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        sendSmsLogin: function(e, i, n, r) {
            t({
                path: "/sm",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        initComponentLogin: function(e, i, n, r) {
            t({
                path: "/ini",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        checkSmsCode: function(e, i, n, r) {
            t({
                path: "/vfcp",
                type: "POST",
                contentType: "application/x-www-form-urlencoded",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        vfsms: function(e, i, n, r) {
            t({
                path: "/vfsms",
                type: "POST",
                contentType: "application/x-www-form-urlencoded",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        getLoginTicket: function(e, i, n, r) {
            t({
                path: "/gt",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        vftcp: function(e, i, n, r) {
            t({
                path: "/vftcp",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        goonlog: function(e, i, n, r) {
            t({
                path: "/log",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-login": function(e, i, n, r) {
            t({
                path: "/lpwd",
                type: "POST",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-gt": function(e, i, n, r) {
            t({
                path: "/gt",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-ini": function(e, i, n, r) {
            t({
                path: "/ini",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-vfcp": function(e, i, n, r) {
            t({
                path: "/vfcp",
                type: "POST",
                contentType: "application/x-www-form-urlencoded",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-cp": function(i, r, t) {
            var e = n;
            if (t) if (t.indexOf("icourse163.org") >= 0) e = "reg." + t + "/dl";
            else e = "passport." + t + "/dl";
            return window.PROTOCOL + e + "/yd/cp?pd=" + i + "&pkid=" + r + "&random=" + MpUtil.uniqueId()
        },
        "mb-lsm": function(e, i, n, r) {
            t({
                path: "/lsm",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-lvfsms": function(e, i, n, r) {
            t({
                path: "/lvfsms",
                type: "POST",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-vftcp": function(e, i, n, r) {
            t({
                path: "/vftcp",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-sms-lvfsms": function(e, i, n, r) {
            t({
                path: "/lvfsms",
                type: "POST",
                contentType: "application/x-www-form-urlencoded",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-sms-lsm": function(e, i, n, r) {
            t({
                path: "/lsm",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-reg-ini": function(e, i, n, r) {
            t({
                path: "/ini",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-reg-chn": function(e, i, n, r) {
            t({
                path: "/chn",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-reg-cp": function(n) {
            var t = window["$regCookieDomain"];
            var i = e;
            if (t) if (t.indexOf("icourse163.org") >= 0) i = "reg." + t + "/zc";
            else i = "passport." + t + "/zc";
            return window.REGPROTOCOL + i + "/yd/cp?channel=2&id=" + n + "&nocache=" + MpUtil.uniqueId()
        },
        "mb-reg-sm": function(e, i, n, r) {
            t({
                path: "/sm",
                type: "GET",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-reg-vfcp": function(e, i, n, r) {
            t({
                path: "/vfcp",
                type: "POST",
                contentType: "application/x-www-form-urlencoded",
                data: e,
                success: i,
                error: n,
                host: r
            })
        },
        "mb-reg-vfsms": function(e, i, n, r) {
            t({
                path: "/vfsms",
                type: "POST",
                data: e,
                success: i,
                error: n,
                host: r
            })
        }
    }
} ();
if ("undefined" == typeof I$) I$ = function() {
    var i = {},
        n = function() {
            return ! 1
        },
        t = {};
    var e = function(t, e) {
        return i.toString.call(t) === "[object " + e + "]"
    };
    return function(f, _) {
        var i = t[f],
            h = e(_, "Function");
        if (null != _ && !h) i = _;
        if (h) {
            var o = [];
            for (var r = 2,
                     c = arguments.length; c > r; r++) o.push(arguments.callee(arguments[r]));
            var u = {};
            o.push.call(o, u, {},
                n, []);
            var s = _.apply(null, o) || u;
            if (!i || !e(s, "Object")) i = s;
            else if (Object.keys) for (var l = Object.keys(s), r = 0, c = l.length, a; c > r; r++) {
                a = l[r];
                i[a] = s[a]
            } else for (var a in s) i[a] = s[a]
        }
        if (null == i) i = {};
        t[f] = i;
        return i
    }
} ();
I$(15,
    function(r, n, e, t) {
        var i = Function.prototype;
        i._$aop = function(i, n) {
            var n = n || e,
                i = i || e,
                r = this;
            return function() {
                var e = {
                    args: t.slice.call(arguments, 0)
                };
                i(e);
                if (!e.stopped) {
                    e.value = r.apply(this, e.args);
                    n(e)
                }
                return e.value
            }
        };
        i._$bind = function() {
            var e = arguments,
                i = arguments[0],
                n = this;
            return function() {
                var r = t.slice.call(e, 1);
                t.push.apply(r, arguments);
                return n.apply(i || null, r)
            }
        };
        i._$bind2 = function() {
            var e = arguments,
                i = t.shift.call(e),
                n = this;
            return function() {
                t.push.apply(arguments, e);
                return n.apply(i || null, arguments)
            }
        };
        var i = String.prototype;
        if (!i.trim) i.trim = function() {
            var t = /(?:^\s+)|(?:\s+$)/g;
            return function() {
                return this.replace(t, "")
            }
        } ();
        if (!this.console) this.console = {
            log: e,
            error: e
        };
        if (!0) {
            NEJ = this.NEJ || {};
            NEJ.copy = function(e, t) {
                e = e || {};
                t = t || n;
                for (var i in t) if (t.hasOwnProperty(i)) e[i] = t[i];
                return e
            };
            NEJ = NEJ.copy(NEJ, {
                O: n,
                R: t,
                F: e,
                P: function(n) {
                    if (!n || !n.length) return null;
                    var t = window;
                    for (var e = n.split("."), r = e.length, i = "window" == e[0] ? 1 : 0; r > i; t = t[e[i]] = t[e[i]] || {},
                        i++);
                    return t
                }
            });
            return NEJ
        }
        return r
    });
I$(40,
    function(t, i, n, e) {
        t.__forIn = function(t, n, a) {
            if (!t || !n) return null;
            var r = Object.keys(t);
            for (var i = 0,
                     o = r.length,
                     e, s; o > i; i++) {
                e = r[i];
                s = n.call(a || null, t[e], e, t);
                if (s) return e
            }
            return null
        };
        t.__forEach = function(t, e, i) {
            t.forEach(e, i)
        };
        t.__col2array = function(t) {
            return e.slice.call(t, 0)
        };
        t.__str2time = function(t) {
            return Date.parse(t)
        };
        return t
    });
I$(33,
    function(_, n, f, l, h) {
        var a = this.navigator.platform,
            i = this.navigator.userAgent;
        var e = {
            mac: a,
            win: a,
            linux: a,
            ipad: i,
            ipod: i,
            iphone: a,
            android: i
        };
        n._$IS = e;
        for (var o in e) e[o] = new RegExp(o, "i").test(e[o]);
        e.ios = e.ipad || e.iphone || e.ipod;
        e.tablet = e.ipad;
        e.desktop = e.mac || e.win || e.linux && !e.android;
        n._$is = function(t) {
            return !! e[t]
        };
        var t = {
            engine: "unknow",
            release: "unknow",
            browser: "unknow",
            version: "unknow",
            prefix: {
                css: "",
                pro: "",
                clz: ""
            }
        };
        n._$KERNEL = t;
        if (/msie\s+(.*?);/i.test(i) || /trident\/.+rv:([\d\.]+)/i.test(i)) {
            t.engine = "trident";
            t.browser = "ie";
            t.version = RegExp.$1;
            t.prefix = {
                css: "ms",
                pro: "ms",
                clz: "MS",
                evt: "MS"
            };
            var r = {
                6 : "2.0",
                7 : "3.0",
                8 : "4.0",
                9 : "5.0",
                10 : "6.0",
                11 : "7.0"
            };
            t.release = r[document.documentMode] || r[parseInt(t.version)]
        } else if (/webkit\/?([\d.]+?)(?=\s|$)/i.test(i)) {
            t.engine = "webkit";
            t.release = RegExp.$1 || "";
            t.prefix = {
                css: "webkit",
                pro: "webkit",
                clz: "WebKit"
            }
        } else if (/rv\:(.*?)\)\s+gecko\//i.test(i)) {
            t.engine = "gecko";
            t.release = RegExp.$1 || "";
            t.browser = "firefox";
            t.prefix = {
                css: "Moz",
                pro: "moz",
                clz: "Moz"
            };
            if (/firefox\/(.*?)(?=\s|$)/i.test(i)) t.version = RegExp.$1 || ""
        } else if (/presto\/(.*?)\s/i.test(i)) {
            t.engine = "presto";
            t.release = RegExp.$1 || "";
            t.browser = "opera";
            t.prefix = {
                css: "O",
                pro: "o",
                clz: "O"
            };
            if (/version\/(.*?)(?=\s|$)/i.test(i)) t.version = RegExp.$1 || ""
        }
        if ("unknow" == t.browser) {
            var r = ["chrome", "maxthon", "safari"];
            for (var s = 0,
                     u = r.length,
                     c; u > s; s++) {
                c = "safari" == r[s] ? "version": r[s];
                if (new RegExp(c + "/(.*?)(?=\\s|$)", "i").test(i)) {
                    t.browser = r[s];
                    t.version = RegExp.$1.trim();
                    break
                }
            }
        }
        n._$SUPPORT = {};
        n._$support = function(t) {
            return !! n._$SUPPORT[t]
        };
        if (!0) _.copy(_.P("nej.p"), n);
        return n
    },
    15);
I$(16,
    function(t, e, i, n, r, s) {
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release <= "4.0") I$(39,
            function() {
                t.__forIn = function(t, i, r) {
                    if (t && i) {
                        var n;
                        for (var e in t) if (t.hasOwnProperty(e)) {
                            n = i.call(r, t[e], e, t);
                            if (n) return e
                        } else;
                    }
                };
                t.__forEach = function(e, i, n) {
                    for (var t = 0,
                             r = e.length; r > t; t++) i.call(n, e[t], t, e)
                };
                t.__col2array = function(t) {
                    var i = [];
                    if (t && t.length) for (var e = 0,
                                                n = t.length; n > e; e++) i.push(t[e]);
                    return i
                };
                t.__str2time = function() {
                    var t = /-/g;
                    return function(e) {
                        return Date.parse(e.replace(t, "/").split(".")[0])
                    }
                } ()
            });
        return t
    },
    40, 33);
I$(1,
    function(t, i, e, n, r, s) {
        e._$klass = function() {
            var t = function() {
                return "[object Function]" !== n.toString.call(arguments[0])
            };
            var e = function(n, t) {
                for (; t;) {
                    var r = t.prototype,
                        e = i.__forIn(r,
                            function(t) {
                                return n === t
                            });
                    if (null != e) return {
                        name: e,
                        klass: t
                    };
                    t = t._$super
                }
            };
            return function() {
                var n = function() {
                    return this.__init.apply(this, arguments)
                };
                n.prototype.__init = r;
                n._$extend = function(a, c) {
                    if (!t(a)) {
                        var u = this;
                        if (c !== !1) i.__forIn(a,
                            function(e, i) {
                                if (!t(e)) u[i] = e
                            });
                        this._$super = a;
                        var o = function() {};
                        o.prototype = a.prototype;
                        this.prototype = new o;
                        this.prototype.constructor = this;
                        var r = [],
                            s = {};
                        var _ = function(i, n) {
                            var t = e(i, n);
                            if (t) {
                                if (r[r.length - 1] != t.name) r.push(t.name);
                                s[t.name] = t.klass._$super;
                                return t.name
                            }
                        };
                        this.prototype.__super = function() {
                            var t = r[r.length - 1],
                                e = arguments.callee.caller;
                            if (!t) t = _(e, this.constructor);
                            else {
                                var i = s[t].prototype;
                                if (!i.hasOwnProperty(e) || e != i[t]) t = _(e, this.constructor);
                                else s[t] = s[t]._$super
                            }
                            var n = s[t].prototype[t].apply(this, arguments);
                            if (t == r[r.length - 1]) {
                                r.pop();
                                delete s[t]
                            }
                            return n
                        };
                        if (!0) {
                            var n = this.prototype;
                            n.__supInit = n.__super;
                            n.__supReset = n.__super;
                            n.__supDestroy = n.__super;
                            n.__supInitNode = n.__super;
                            n.__supDoBuild = n.__super;
                            n.__supOnShow = n.__super;
                            n.__supOnHide = n.__super;
                            n.__supOnRefresh = n.__super;
                            this._$supro = a.prototype
                        }
                        return this.prototype
                    }
                };
                return n
            }
        } ();
        if (!0) {
            t.C = e._$klass;
            t.copy(this.NEJ, t)
        }
        return e
    },
    15, 16);
I$(4,
    function(e, n, t, r, s, a) {
        var i = function(e, t) {
            try {
                t = t.toLowerCase();
                if (null === e) return "null" == t;
                if (void 0 === e) return "undefined" == t;
                else return r.toString.call(e).toLowerCase() == "[object " + t + "]"
            } catch(i) {
                return ! 1
            }
        };
        t._$isFunction = function(t) {
            return i(t, "function")
        };
        t._$isString = function(t) {
            return i(t, "string")
        };
        t._$isNumber = function(t) {
            return i(t, "number")
        };
        t._$isBoolean = function(t) {
            return i(t, "boolean")
        };
        t._$isDate = function(t) {
            return i(t, "date")
        };
        t._$isArray = function(t) {
            return i(t, "array")
        };
        t._$isObject = function(t) {
            return i(t, "object")
        };
        t._$length = function() {
            var t = /[^\x00-\xff]/g;
            return function(e) {
                return ("" + (e || "")).replace(t, "**").length
            }
        } ();
        t._$loop = function(e, i, r) {
            if (t._$isObject(e) && t._$isFunction(i)) return n.__forIn.apply(n, arguments);
            else return null
        };
        t._$indexOf = function(n, e) {
            var r = t._$isFunction(e) ? e: function(t) {
                    return t === e
                },
                i = t._$forIn(n, r);
            return null != i ? i: -1
        };
        t._$binSearch = function() {
            var e;
            var t = function(n, r, s) {
                if (r > s) return - 1;
                var i = Math.ceil((r + s) / 2),
                    a = e(n[i], i, n);
                if (0 == a) return i;
                if (0 > a) return t(n, r, i - 1);
                else return t(n, i + 1, s)
            };
            return function(i, n) {
                e = n || s;
                return t(i, 0, i.length - 1)
            }
        } ();
        t._$reverseEach = function(e, n, r) {
            if (e && e.length && t._$isFunction(n)) for (var i = e.length - 1; i >= 0; i--) if (n.call(r, e[i], i, e)) return i;
            return null
        };
        t._$forEach = function(e, i, r) {
            if (e && e.length && t._$isFunction(i)) if (!e.forEach) t._$forIn.apply(t, arguments);
            else n.__forEach(e, i, r)
        };
        t._$forIn = function(e, n, r) {
            if (!e || !t._$isFunction(n)) return null;
            if (t._$isNumber(e.length)) {
                for (var i = 0,
                         s = e.length; s > i; i++) if (n.call(r, e[i], i, e)) return i
            } else if (t._$isObject(e)) return t._$loop(e, n, r);
            return null
        };
        t._$encode = function(e, t) {
            t = "" + t;
            if (!e || !t) return t || "";
            else return t.replace(e.r,
                function(t) {
                    var i = e[!e.i ? t.toLowerCase() : t];
                    return null != i ? i: t
                })
        };
        t._$escape = function() {
            var e = /<br\/?>$/,
                i = {
                    r: /\<|\>|\&|\r|\n|\s|\'|\"/g,
                    "<": "&lt;",
                    ">": "&gt;",
                    "&": "&amp;",
                    " ": "&nbsp;",
                    '"': "&quot;",
                    "'": "&#39;",
                    "\n": "<br/>",
                    "\r": ""
                };
            return function(n) {
                n = t._$encode(i, n);
                return n.replace(e, "<br/><br/>")
            }
        } ();
        t._$unescape = function() {
            var e = {
                r: /\&(?:lt|gt|amp|nbsp|#39|quot)\;|\<br\/\>/gi,
                "&lt;": "<",
                "&gt;": ">",
                "&amp;": "&",
                "&nbsp;": " ",
                "&#39;": "'",
                "&quot;": '"',
                "<br/>": "\n"
            };
            return function(i) {
                return t._$encode(e, i)
            }
        } ();
        t._$format = function() {
            var e = {
                    i: !0,
                    r: /\byyyy|yy|MM|cM|eM|M|dd|d|HH|H|mm|ms|ss|m|s|w|ct|et\b/g
                },
                n = ["上午", "下午"],
                r = ["A.M.", "P.M."],
                s = ["日", "一", "二", "三", "四", "五", "六"],
                a = ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"],
                o = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"];
            var i = function(t) {
                t = parseInt(t) || 0;
                return (10 > t ? "0": "") + t
            };
            var _ = function(t) {
                return 12 > t ? 0 : 1
            };
            return function(c, u, l) {
                if (!c || !u) return "";
                c = t._$var2date(c);
                e.yyyy = c.getFullYear();
                e.yy = ("" + e.yyyy).substr(2);
                e.M = c.getMonth() + 1;
                e.MM = i(e.M);
                e.eM = o[e.M - 1];
                e.cM = a[e.M - 1];
                e.d = c.getDate();
                e.dd = i(e.d);
                e.H = c.getHours();
                e.HH = i(e.H);
                e.m = c.getMinutes();
                e.mm = i(e.m);
                e.s = c.getSeconds();
                e.ss = i(e.s);
                e.ms = c.getMilliseconds();
                e.w = s[c.getDay()];
                var h = _(e.H);
                e.ct = n[h];
                e.et = r[h];
                if (l) e.H = e.H % 12;
                return t._$encode(e, u)
            }
        } ();
        t._$var2date = function(e) {
            var i = e;
            if (t._$isString(e)) i = new Date(n.__str2time(e));
            if (!t._$isDate(i)) i = new Date(e);
            return i
        };
        t._$fixed = function(t, e) {
            return parseFloat(new Number(t).toFixed(e))
        };
        t._$absolute = function() {
            var r = /([^\/:])\/.*$/,
                s = /\/[^\/]+$/,
                a = /[#\?]/,
                t = location.href.split(/[?#]/)[0],
                e = document.createElement("a");
            var i = function(t) {
                return (t || "").indexOf("://") > 0
            };
            var n = function(t) {
                return (t || "").split(a)[0].replace(s, "/")
            };
            var o = function(t, e) {
                if (0 == t.indexOf("/")) return e.replace(r, "$1") + t;
                else return n(e) + t
            };
            t = n(t);
            return function(n, r) {
                n = (n || "").trim();
                if (!i(r)) r = t;
                if (!n) return r;
                if (i(n)) return n;
                n = o(n, r);
                e.href = n;
                n = e.href;
                return i(n) ? n: e.getAttribute("href", 4)
            }
        } ();
        t._$url2origin = function() {
            var t = /^([\w]+?:\/\/.*?(?=\/|$))/i;
            return function(e) {
                if (t.test(e || "")) return RegExp.$1.toLowerCase();
                else return ""
            }
        } ();
        t._$string2object = function(i, n) {
            var e = {};
            t._$forEach((i || "").split(n),
                function(n) {
                    var t = n.split("=");
                    if (t && t.length) {
                        var i = t.shift();
                        if (i) e[decodeURIComponent(i)] = decodeURIComponent(t.join("="))
                    }
                });
            return e
        };
        t._$object2string = function(e, n, r) {
            if (!e) return "";
            var i = [];
            t._$loop(e,
                function(e, n) {
                    if (!t._$isFunction(e)) {
                        if (t._$isDate(e)) e = e.getTime();
                        else if (t._$isArray(e)) e = e.join(",");
                        else if (t._$isObject(e)) e = JSON.stringify(e);
                        if (r) e = encodeURIComponent(e);
                        i.push(encodeURIComponent(n) + "=" + e)
                    }
                });
            return i.join(n || ",")
        };
        t._$query2object = function(e) {
            return t._$string2object(e, "&")
        };
        t._$object2query = function(e) {
            return t._$object2string(e, "&", !0)
        };
        t._$object2array = function(t) {
            return n.__col2array(t)
        };
        t._$array2object = function(n, e) {
            var i = {};
            t._$forEach(n,
                function(t) {
                    var n = t;
                    if (e) n = e(t);
                    if (null != n) i[n] = t
                });
            return i
        };
        t._$number2string = function(t, i) {
            var n = ("" + t).length,
                r = Math.max(1, parseInt(i) || 0),
                e = r - n;
            if (e > 0) t = new Array(e + 1).join("0") + t;
            return "" + t
        };
        t._$safeDelete = function(i, e) {
            if (!t._$isArray(e)) try {
                delete i[e]
            } catch(n) {
                i[e] = void 0
            } else t._$forEach(e,
                function(e) {
                    t._$safeDelete(i, e)
                })
        };
        t._$randString = function() {
            var t = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
            return function(e) {
                e = e || 10;
                var i = [];
                for (var n = 0,
                         r; e > n; ++n) {
                    r = Math.floor(Math.random() * t.length);
                    i.push(t.charAt(r))
                }
                return i.join("")
            }
        } ();
        t._$randNumber = function(t, e) {
            return Math.floor(Math.random() * (e - t) + t)
        };
        t._$randNumberString = function(e) {
            e = Math.max(0, Math.min(e || 8, 30));
            var i = Math.pow(10, e - 1),
                n = 10 * i;
            return t._$randNumber(i, n).toString()
        };
        t._$uniqueID = function() {
            var t = +new Date;
            return function() {
                return "" + t++
            }
        } ();
        t._$query = function(t, n) {
            t = t || r;
            var i = (n || "").split(".");
            for (var e = 0,
                     s = i.length; s > e; e++) {
                t = t[i[e]];
                if (!t) break
            }
            return t
        };
        t._$merge = function() {
            var e = arguments.length - 1,
                i = arguments[e];
            if (t._$isFunction(i)) e -= 1;
            else i = s;
            var r = arguments[0] || {};
            for (var n = 1; e >= n; n++) t._$loop(arguments[n],
                function(t, e) {
                    if (!i(t, e)) r[e] = t
                });
            return r
        };
        t._$fetch = function(e, i) {
            if (i) t._$loop(e,
                function(r, t, n) {
                    var e = i[t];
                    if (null != e) n[t] = e
                });
            return e
        };
        t._$hasProperty = function(e) {
            if (!e) return ! 1;
            if (null != e.length) return e.length > 0;
            var i = 0;
            t._$loop(e,
                function() {
                    i++;
                    return i > 0
                });
            return i > 0
        };
        if (!0) {
            e.Q = t._$query;
            e.X = t._$merge;
            e.EX = t._$fetch;
            e.copy(this.NEJ, e);
            e.copy(e.P("nej.u"), t)
        }
        return t
    },
    15, 16);
I$(106,
    function(s, n, t, e, a, o) {
        var i = {};
        t.__url2host = function() {
            var t = /^([\w]+?:\/\/.*?(?=\/|$))/i;
            return function(e) {
                e = e || "";
                if (t.test(e)) return RegExp.$1;
                else return location.protocol + "//" + location.host
            }
        } ();
        t.__set = function(t, e) {
            i[t] = e
        };
        t.__get = function(t) {
            return i[t]
        };
        var r = function() {
            var r = {
                portrait: {
                    name: "portrait",
                    dft: "portrait/"
                },
                "ajax.swf": {
                    name: "ajax",
                    dft: "nej_proxy_flash.swf"
                },
                "chart.swf": {
                    name: "chart",
                    dft: "nej_flex_chart.swf"
                },
                "audio.swf": {
                    name: "audio",
                    dft: "nej_player_audio.swf"
                },
                "video.swf": {
                    name: "video",
                    dft: "nej_player_video.swf"
                },
                "clipboard.swf": {
                    name: "clipboard",
                    dft: "nej_clipboard.swf"
                },
                "upload.image.swf": {
                    name: "uploadimage",
                    dft: "nej_upload_image.swf"
                }
            };
            var i = function(e) {
                var n = {};
                if (!e || !e.length) return n;
                for (var r = 0,
                         s = e.length,
                         i; s > r; r++) {
                    i = e[r];
                    if (i.indexOf("://") > 0) n[t.__url2host(i)] = i
                }
                return n
            };
            return function(a) {
                t.__set("root", a.root || "/res/");
                var o = t.__get("root");
                n._$loop(r,
                    function(e, i, n) {
                        t.__set(i, a[e.name] || o + e.dft)
                    });
                var s = a.p_csrf;
                if (s === !0) s = {
                    cookie: "AntiCSRF",
                    param: "AntiCSRF"
                };
                s = s || e;
                t.__set("csrf", {
                    param: s.param || "",
                    cookie: s.cookie || ""
                });
                t.__set("frames", i(a.p_frame));
                t.__set("flashs", i(a.p_flash))
            }
        } ();
        r(this.NEJ_CONF || e);
        return t
    },
    15, 4);
I$(78,
    function(t, e, n, i, r, s) {
        if ("trident" === e._$KERNEL.engine) I$(104,
            function() {
                t.__set("storage.swf", (this.NEJ_CONF || i).storage || t.__get("root") + "nej_storage.swf")
            });
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release <= "3.0") I$(105,
            function() {
                t.__set("blank.png", (this.NEJ_CONF || i).blank || t.__get("root") + "nej_blank.gif")
            });
        return t
    },
    106, 33);
I$(43,
    function(i, e, t, n, r, s) {
        t._$getFrameProxy = function(n) {
            var i = e.__url2host(n);
            return t._$get("frames")[i] || i + "/res/nej_proxy_frame.html"
        };
        t._$getFlashProxy = function(i) {
            return t._$get("flashs")[e.__url2host(i)]
        };
        t._$get = function(t) {
            return e.__get(t)
        };
        if (!0) i.copy(i.P("nej.c"), t);
        return t
    },
    15, 78);
I$(19,
    function(i, n, t, r, s, a) {
        var e = +new Date;
        t._$CODE_NOTFUND = 1e4 - e;
        t._$CODE_NOTASGN = 10001 - e;
        t._$CODE_NOTSPOT = 10002 - e;
        t._$CODE_TIMEOUT = 10003 - e;
        t._$CODE_ERREVAL = 10004 - e;
        t._$CODE_ERRCABK = 10005 - e;
        t._$CODE_ERRSERV = 10006 - e;
        t._$CODE_ERRABRT = 10007 - e;
        t._$HEAD_CT = "Content-Type";
        t._$HEAD_CT_PLAN = "text/plain";
        t._$HEAD_CT_FILE = "multipart/form-data";
        t._$HEAD_CT_FORM = "application/x-www-form-urlencoded";
        t._$BLANK_IMAGE = n._$get("blank.png") || "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";
        if (!0) i.copy(i.P("nej.g"), t);
        return t
    },
    15, 43);
I$(17,
    function(i, t) {
        var e = {};
        t._$merge = function(t) {
            i._$merge(e, t)
        };
        t._$dump = function() {
            return e
        };
        t._$clear = function() {
            e = {}
        };
        return t
    },
    4);
I$(42,
    function(e, i, t, n, r, s) {
        t.__checkEvent = function() {
            var e = {
                    touchstart: "mousedown",
                    touchmove: "mousemove",
                    touchend: "mouseup"
                },
                t = i._$KERNEL.prefix,
                n = {
                    transitionend: "TransitionEnd",
                    animationend: "AnimationEnd",
                    animationstart: "AnimationStart",
                    animationiteration: "AnimationIteration",
                    visibilitychange: "visibilitychange"
                };
            var r = {
                enter: function(i, n, t) {
                    var e = {
                        type: "keypress"
                    };
                    if (t) e.handler = function(e) {
                        if (13 === e.keyCode) t.call(i, e)
                    };
                    return e
                }
            };
            var s = function(e) {
                return (t.evt || t.pro) + e
            };
            return function(_, t, c) {
                var i = {
                    type: t,
                    handler: c
                };
                if (! ("on" + t in _)) {
                    var a = e[t];
                    if (a) {
                        i.type = a;
                        return i
                    }
                    var a = n[t];
                    if (a) {
                        i.type = s(a);
                        return i
                    }
                    var o = r[t];
                    if (o) return o.apply(null, arguments)
                }
                return i
            }
        } ();
        t.__addEvent = function() {
            var t = arguments;
            if (!1) if (! ("on" + t[1] in t[0])) console.log("not support event[" + t[1] + "] for " + t[0]);
            t[0].addEventListener(t[1], t[2], t[3])
        };
        t.__delEvent = function() {
            var t = arguments;
            t[0].removeEventListener(t[1], t[2], t[3])
        };
        t.__dispatchEvent = function(i, n, r) {
            var t = document.createEvent("Event");
            t.initEvent(n, !0, !0);
            e._$merge(t, r);
            i.dispatchEvent(t)
        };
        return t
    },
    4, 33);
I$(18,
    function(e, t, i, n, r, s, a) {
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release >= "6.0") I$(44,
            function() {
                t.__checkEvent = function() {
                    var e = {
                        touchcancel: "MSPointerCancel",
                        touchstart: "MSPointerDown",
                        touchmove: "MSPointerMove",
                        touchend: "MSPointerUp"
                    };
                    return t.__checkEvent._$aop(function(t) {
                        var i = t.args;
                        var n = e[i[1]];
                        if (n) {
                            t.stopped = !0;
                            t.value = {
                                type: n,
                                handler: i[2]
                            }
                        }
                    })
                } ()
            });
        if ("trident" === e._$KERNEL.engine && "5.0" == e._$KERNEL.release) I$(45,
            function() {
                t.__checkEvent = function() {
                    var e = {};
                    var i = {
                        input: function(t, n, i) {
                            if (!i) return {
                                type: n
                            };
                            else return {
                                type: n,
                                handler: function(n) {
                                    var r = t.id;
                                    e[r] = t.value;
                                    i.call(t, n)
                                },
                                link: [[document, "selectionchange",
                                    function(r) {
                                        var n = t.id;
                                        if (t == document.activeElement) {
                                            if (e[n] !== t.value) {
                                                e[n] = t.value;
                                                i.call(t, r)
                                            }
                                        } else delete e[n]
                                    }]]
                            }
                        }
                    };
                    return t.__checkEvent._$aop(function(t) {
                        var e = t.args;
                        var n = i[e[1]];
                        if (n) {
                            t.stopped = !0;
                            t.value = n.apply(null, e)
                        }
                    })
                } ()
            });
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release >= "5.0") I$(46,
            function() {
                var e = {
                    propertychange: 1
                };
                t.__addEvent = t.__addEvent._$aop(function(i) {
                    var t = i.args;
                    if (null != e[t[1]] && t[0].attachEvent) {
                        i.stopped = !0;
                        t[0].attachEvent("on" + t[1], t[2])
                    }
                });
                t.__delEvent = t.__delEvent._$aop(function(i) {
                    var t = i.args,
                        n = e[t[1]];
                    if (null != e[t[1]] && t[0].detachEvent) {
                        i.stopped = !0;
                        t[0].detachEvent("on" + t[1], t[2])
                    }
                })
            });
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release <= "4.0") I$(47,
            function() {
                t.__checkEvent = function() {
                    var e = {};
                    var i = {
                        input: function(t, a, r) {
                            var n = {
                                type: "propertychange"
                            };
                            if (r) {
                                var i = t.id;
                                var s = function(n) {
                                    if (t.value && !e["x-" + i]) {
                                        e["x-" + i] = !0;
                                        r.call(t, n)
                                    }
                                };
                                n.handler = function(n) {
                                    if ("value" in t && "value" == n.propertyName) {
                                        if (e[i]) return;
                                        e[i] = !0;
                                        r.call(t, n);
                                        delete e[i]
                                    }
                                };
                                n.link = [[t, "keyup", s], [t, "mouseup", s], [t, "mousemove", s]];
                                n.destroy = function() {
                                    delete e[i];
                                    delete e["x-" + i]
                                }
                            }
                            return n
                        },
                        load: function(t, n, e) {
                            var i = {
                                type: "readystatechange"
                            };
                            if (e) i.handler = function(i) {
                                if ("loaded" == t.readyState || "complete" == t.readyState) e.call(t, i)
                            };
                            return i
                        }
                    };
                    return t.__checkEvent._$aop(function(e) {
                        var t = e.args;
                        var n = i[t[1]];
                        if (n) {
                            e.stopped = !0;
                            e.value = n.apply(null, t)
                        }
                        if (t[2]) t[2] = t[2]._$bind(t[0])
                    })
                } ();
                t.__addEvent = function() {
                    var t = arguments;
                    if (!1) if (! ("on" + t[1] in t[0])) console.log("not support event[" + t[1] + "] for " + t[0]);
                    t[0].attachEvent("on" + t[1], t[2])
                };
                t.__delEvent = function() {
                    var t = arguments;
                    t[0].detachEvent("on" + t[1], t[2])
                };
                t.__dispatchEvent = function() {
                    var t = {
                        propertychange: {
                            propertyName: "value"
                        }
                    };
                    return function(s, e, a) {
                        var n = document.createEventObject();
                        try {
                            i._$merge(n, t[e], a);
                            s.fireEvent("on" + e, n)
                        } catch(r) {
                            console.error(r.message);
                            console.error(r.stack)
                        }
                    }
                } ()
            });
        if ("gecko" === e._$KERNEL.engine) I$(48,
            function() {
                t.__checkEvent = function() {
                    var e = /^(?:transitionend|animationend|animationstart|animationiteration)$/i;
                    var i = {
                        mousewheel: function(i, n, t) {
                            var e = {
                                type: "MozMousePixelScroll"
                            };
                            if (t) e.handler = function(e) {
                                var n = e.detail;
                                e.wheelDelta = -n;
                                e.wheelDeltaY = -n;
                                e.wheelDeltaX = 0;
                                t.call(i, e)
                            };
                            return e
                        }
                    };
                    return t.__checkEvent._$aop(function(t) {
                        var n = t.args;
                        if (e.test(n[1])) {
                            t.stopped = !0;
                            t.value = {
                                type: n[1],
                                handler: n[2]
                            }
                        }
                        var r = i[n[1]];
                        if (r) {
                            t.stopped = !0;
                            t.value = r.apply(null, n)
                        }
                    })
                } ()
            });
        return t
    },
    33, 42, 4);
I$(3,
    function(_, e, i, l, n, t, h, d, f) {
        var r = {},
            s = {};
        var o = function() {
            var t = /[\s,;]+/;
            return function(e) {
                var e = (e || "").trim().toLowerCase();
                return ! e ? null: e.split(t)
            }
        } ();
        var a = function(t, i, r) {
            var n = "page" + i;
            return null != t[n] ? t[n] : t["client" + i] + e._$getPageBox()["scroll" + r]
        };
        var c = function(e, n, i) {
            var r = "scroll" + i;
            _node = t._$getElement(e),
                _xret = a(e, n, i);
            for (; _node && _node != document.body && _node != document.documentElement;) {
                _xret += _node[r] || 0;
                _node = _node.parentNode
            }
            return _xret
        };
        var u = function(t, r, s, a) {
            var n = {};
            t = e._$get(t);
            if (!t) return null;
            e._$id(t);
            n.element = t;
            if (!i._$isFunction(s)) return null;
            n.handler = s;
            var r = o(r);
            if (!r) return null;
            n.type = r;
            n.capture = !!a;
            return n
        };
        t._$addEvent = s._$addEvent = function() {
            var t = function(n, t, i) {
                var a = e._$id(t.element),
                    s = r[a] || {},
                    o = s[n] || [];
                o.push({
                    type: i.type || n,
                    func: i.handler || t.handler,
                    sfun: t.handler,
                    capt: t.capture,
                    link: i.link,
                    destroy: i.destroy
                });
                s[n] = o;
                r[a] = s
            };
            return function() {
                var r = u.apply(null, arguments);
                if (r) i._$forEach(r.type,
                    function(a) {
                        var s = n.__checkEvent(r.element, a, r.handler);
                        n.__addEvent(r.element, s.type, s.handler, r.capture);
                        i._$forIn(s.link,
                            function(t) {
                                t[3] = !!t[3];
                                n.__addEvent.apply(n, t);
                                t[0] = e._$id(t[0])
                            });
                        t(a, r, s)
                    })
            }
        } ();
        t._$delEvent = s._$delEvent = function() {
            var t = function(_, n) {
                var c = e._$id(n.element),
                    a = r[c] || h,
                    o = a[_],
                    u = i._$indexOf(o,
                        function(t) {
                            return t.sfun === n.handler && t.capt === n.capture
                        });
                var s = null;
                if (u >= 0) {
                    var t = o.splice(u, 1)[0];
                    s = [[n.element, t.type, t.func, n.capture]];
                    if (t.link) {
                        i._$forEach(t.link,
                            function(t) {
                                t[0] = e._$get(t[0])
                            });
                        s.push.apply(s, t.link)
                    }
                    if (t.destroy) t.destroy();
                    if (!o.length) delete a[_];
                    if (!i._$hasProperty(a)) delete r[c]
                }
                return s
            };
            return function() {
                var e = u.apply(null, arguments);
                if (e) i._$forEach(e.type,
                    function(r) {
                        i._$forEach(t(r, e),
                            function(t) {
                                n.__delEvent.apply(n, t)
                            })
                    })
            }
        } ();
        t._$clearEvent = s._$clearEvent = function() {
            var n = function(e, n, r) {
                i._$reverseEach(r,
                    function(i) {
                        t._$delEvent(e, n, i.sfun, i.capt)
                    })
            };
            return function(c, s) {
                var a = e._$id(c);
                if (a) {
                    var _ = r[a];
                    if (_) {
                        s = o(s);
                        if (s) i._$forEach(s,
                            function(t) {
                                n(a, t, _[t])
                            });
                        else i._$loop(_,
                            function(i, e) {
                                t._$clearEvent(c, e)
                            })
                    }
                }
            }
        } ();
        t._$dispatchEvent = s._$dispatchEvent = function(t, r, s) {
            var t = e._$get(t);
            if (t) i._$forEach(o(r),
                function(e) {
                    var i = n.__checkEvent(t, e);
                    n.__dispatchEvent(t, i.type, s)
                })
        };
        t._$getElement = function() {
            var t;
            var n = function(i, n) {
                var r = i.split(":");
                if (r.length > 1) {
                    if (!t) t = {
                        a: e._$attr,
                        d: e._$dataset,
                        c: e._$hasClassName,
                        t: function(t, e) {
                            return (t.tagName || "").toLowerCase() === e
                        }
                    };
                    var s = t[r[0]];
                    if (s) return !! s(n, r[1]);
                    i = r[1]
                }
                return !! e._$attr(n, i) || !!e._$dataset(n, i) || e._$hasClassName(n, i)
            };
            return function(r) {
                if (!r) return null;
                var t = r.target || r.srcElement,
                    e = arguments[1];
                if (!e) return t;
                if (i._$isString(e)) e = n._$bind(null, e);
                if (i._$isFunction(e)) {
                    for (; t;) {
                        if (e(t)) return t;
                        t = t.parentNode
                    }
                    return null
                }
                return t
            }
        } ();
        t._$stop = function(e) {
            t._$stopBubble(e);
            t._$stopDefault(e)
        };
        t._$stopBubble = function(t) {
            if (t) t.stopPropagation ? t.stopPropagation() : t.cancelBubble = !0
        };
        t._$stopDefault = function(t) {
            if (t) t.preventDefault ? t.preventDefault() : t.returnValue = !1
        };
        t._$page = function(e) {
            return {
                x: t._$pageX(e),
                y: t._$pageY(e)
            }
        };
        t._$pageX = function(t) {
            return c(t, "X", "Left")
        };
        t._$pageY = function(t) {
            return c(t, "Y", "Top")
        };
        t._$clientX = function(t) {
            return a(t, "X", "Left")
        };
        t._$clientY = function(t) {
            return a(t, "Y", "Top")
        };
        l._$merge(s);
        if (!0) _.copy(_.P("nej.v"), t);
        return t
    },
    15, 2, 4, 17, 18);
I$(41,
    function(e, r, t, s, a, o) {
        t.__getElementById = function(t, e) {
            if (t.getElementById) return t.getElementById("" + e);
            try {
                return t.querySelector("#" + e)
            } catch(i) {
                return null
            }
        };
        t.__getChildren = function(t) {
            return e._$object2array(t.children)
        };
        t.__getElementsByClassName = function(t, i) {
            return e._$object2array(t.getElementsByClassName(i))
        };
        t.__nextSibling = function(t) {
            return t.nextElementSibling
        };
        t.__previousSibling = function(t) {
            return t.previousElementSibling
        };
        t.__dataset = function(t, e, i) {
            t.dataset = t.dataset || {};
            if (void 0 !== i) t.dataset[e] = i;
            return t.dataset[e]
        };
        t.__getAttribute = function(t, e) {
            return t.getAttribute(e)
        };
        t.__serializeDOM2XML = function(t) {
            return (new XMLSerializer).serializeToString(t) || ""
        };
        t.__parseDOMFromXML = function(e) {
            var t = (new DOMParser).parseFromString(e, "text/xml").documentElement;
            return "parsererror" == t.nodeName ? null: t
        };
        t.__fullScreen = function() {};
        t.__mask = function() {};
        t.__unmask = function() {};
        var i = r._$SUPPORT,
            n = r._$KERNEL.prefix;
        t.__isMatchedName = function() {
            var t = /^([a-z]+?)[A-Z]/;
            return function(e, i) {
                return !! (i[e] || t.test(e) && i[RegExp.$1]);
            }
        } ();
        t.__isNeedPrefixed = function() {
            var i = e._$array2object(["animation", "transform", "transition", "appearance", "userSelect", "box", "flex", "column"]);
            return function(e) {
                return t.__isMatchedName(e, i)
            }
        } ();
        t.__fmtStyleName = function() {
            var t = /-([a-z])/g;
            return function(e) {
                e = e || "";
                return e.replace(t,
                    function(e, t) {
                        return t.toUpperCase()
                    })
            }
        } ();
        t.__getStyleName = function() {
            var e = /^[a-z]/,
                i = n.css || "";
            return function(n) {
                n = t.__fmtStyleName(n);
                if (!t.__isNeedPrefixed(n)) return n;
                else return i + n.replace(e,
                        function(t) {
                            return t.toUpperCase()
                        })
            }
        } ();
        t.__getStyleValue = function(e, i) {
            var n = window.getComputedStyle(e, null);
            return n[t.__getStyleName(i)] || ""
        };
        t.__setStyleValue = function(e, i, n) {
            e.style[t.__getStyleName(i)] = n
        };
        t.__getCSSMatrix = function() {
            var t = /\((.*?)\)/,
                i = /\s*,\s*/,
                r = ["CSSMatrix", n.clz + "CSSMatrix"],
                s = ["m11", "m12", "m21", "m22", "m41", "m42"];
            var a = function(r) {
                var n = {};
                if (t.test(r || "")) e._$forEach(RegExp.$1.split(i),
                    function(t, e) {
                        n[s[e]] = t
                    });
                return n
            };
            return function(i) {
                var t;
                e._$forIn(r,
                    function(e) {
                        if (this[e]) {
                            t = new this[e](i || "");
                            return ! 0
                        }
                    });
                return ! t ? a(i) : t
            }
        } ();
        t.__injectCSSText = function(t, e) {
            t.textContent = e
        };
        t.__processCSSText = function() {
            var r = /\$<(.*?)>/gi,
                a = /\{(.*?)\}/g,
                o = "-" + n.css.toLowerCase() + "-",
                _ = {
                    scale: "scale({x|1},{y|1})",
                    rotate: "rotate({a})",
                    translate: "translate({x},{y})",
                    matrix: "matrix({m11},{m12},{m21},{m22},{m41},{m42})"
                },
                c = {
                    scale: "scale3d({x|1},{y|1},{z|1})",
                    rotate: "rotate3d({x},{y},{z},{a})",
                    translate: "translate3d({x},{y},{z})",
                    matrix: "matrix3d({m11},{m12},{m13},{m14},{m21},{m22},{m23},{m24},{m31},{m32},{m33|1},{m34},{m41},{m42},{m43},{m44|1})"
                };
            var u = function(e, t) {
                t = t || s;
                return e.replace(a,
                    function(n, i) {
                        var e = i.split("|");
                        return t[e[0]] || e[1] || "0"
                    })
            };
            t.__processTransformValue = function(e, n) {
                var t = (!i.css3d ? _: c)[e.trim()];
                if (t) return u(t, n);
                else return ""
            };
            return function(i) {
                if (!i.replace) return i;
                else return i.replace(r,
                    function(r, i) {
                        if ("vendor" === i) return o;
                        var n = (i || "").split("|");
                        return t.__processTransformValue(n[0], e._$query2object(n[1])) || r
                    })
            }
        } ();
        t.__appendCSSText = function(i, n) {
            var t = i.sheet,
                e = t.cssRules.length;
            t.insertRule(n, e);
            return t.cssRules[e]
        };
        t.__getClassList = function() {
            var t = /\s+/;
            return function(e) {
                e = (e || "").trim();
                return e ? e.split(t) : null
            }
        } ();
        t.__processClassName = function(i, n, r) {
            if ("replace" != n) e._$forEach(t.__getClassList(r),
                function(t) {
                    i.classList[n](t)
                });
            else {
                t.__processClassName(i, "remove", r);
                t.__processClassName(i, "add", arguments[3])
            }
        };
        t.__hasClassName = function(n, r) {
            var i = n.classList;
            if (!i || !i.length) return ! 1;
            else return e._$indexOf(t.__getClassList(r),
                    function(t) {
                        return i.contains(t)
                    }) >= 0
        }; !
            function() {
                if (!i.css3d) {
                    var e = t.__getCSSMatrix();
                    i.css3d = !!e && null != e.m41
                }
            } ();
        return t
    },
    4, 33);
I$(20,
    function(t, e, i, n, r, s, a) {
        if ("trident" === e._$KERNEL.engine) I$(49,
            function() {
                t.__getChildren = t.__getChildren._$aop(function(t) {
                    var e = t.args[0];
                    if (!e.children) {
                        t.stopped = !0;
                        var n = [];
                        i._$forEach(e.childNodes,
                            function(t) {
                                if (1 == t.nodeType) n.push(t)
                            });
                        t.value = n
                    }
                })
            });
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release <= "6.0") I$(50,
            function() {
                t.__dataset = function() {
                    var t = {},
                        e = "data-",
                        n = /\-(.{1})/gi;
                    var r = function(r) {
                        var s = r.id;
                        if (!t[s]) {
                            var a = {};
                            i._$forEach(r.attributes,
                                function(i) {
                                    var t = i.nodeName;
                                    if (0 == t.indexOf(e)) {
                                        t = t.replace(e, "").replace(n,
                                            function(e, t) {
                                                return t.toUpperCase()
                                            });
                                        a[t] = i.nodeValue || ""
                                    }
                                });
                            t[s] = a
                        }
                    };
                    return function(e, i, n) {
                        r(e);
                        var s = t[e.id];
                        if (void 0 !== n) s[i] = n;
                        return s[i]
                    }
                } ()
            });
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release <= "5.0") I$(51,
            function() {
                try {
                    document.execCommand("BackgroundImageCache", !1, !0)
                } catch(e) {}
                t.__injectCSSText = function() {
                    var e = 30;
                    return t.__injectCSSText._$aop(function(i) {
                        var t = i.args[0];
                        if (t.styleSheet) {
                            i.stopped = !0;
                            var n = i.args[1];
                            var r = document.styleSheets;
                            if (r.length > e) {
                                t = r[e];
                                n = t.cssText + n
                            } else t = t.styleSheet;
                            t.cssText = n
                        }
                    })
                } ();
                t.__getClassRegExp = function() {
                    var t = /\s+/g;
                    return function(e) {
                        e = (e || "").trim().replace(t, "|");
                        return ! e ? null: new RegExp("(\\s|^)(?:" + e + ")(?=\\s|$)", "g")
                    }
                } ();
                t.__processClassName = function(n, a, e) {
                    e = e || "";
                    var r = n.className || "",
                        s = t.__getClassRegExp(e + " " + (arguments[3] || ""));
                    var i = r;
                    if (s) i = i.replace(s, "");
                    switch (a) {
                        case "remove":
                            e = "";
                            break;
                        case "replace":
                            e = arguments[3] || ""
                    }
                    i = (i + " " + e).trim();
                    if (r != i) n.className = i
                };
                t.__hasClassName = function(i, n) {
                    var e = t.__getClassRegExp(n);
                    if (e) return e.test(i.className || "");
                    else return ! 1
                }
            });
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release <= "4.0") I$(52,
            function() {
                t.__getElementsByClassName = function(e, n) {
                    var t = [],
                        r = new RegExp("(\\s|^)(?:" + n.replace(/\s+/g, "|") + ")(?=\\s|$)");
                    i._$forEach(e.getElementsByTagName("*"),
                        function(e) {
                            if (r.test(e.className)) t.push(e)
                        });
                    return t
                };
                t.__nextSibling = function(t) {
                    for (; t = t.nextSibling;) if (1 == t.nodeType) return t
                };
                t.__previousSibling = function(t) {
                    for (; t = t.previousSibling;) if (1 == t.nodeType) return t
                };
                t.__serializeDOM2XML = function(t) {
                    return "xml" in t ? t.xml: t.outerHTML
                };
                t.__parseDOMFromXML = function() {
                    var t = ["Msxml2.DOMDocument.6.0", "Msxml2.DOMDocument.3.0"];
                    var e = function() {
                        try {
                            for (var e = 0,
                                     i = t.length; i > e; e++) return new ActiveXObject(t[e])
                        } catch(n) {
                            return null
                        }
                    };
                    return function(i) {
                        var t = e();
                        if (t && t.loadXML(i) && !t.parseError.errorCode) return t.documentElement;
                        else return null
                    }
                } ();
                t.__getStyleValue = function() {
                    var e = /opacity\s*=\s*([\d]+)/i;
                    var i = {
                        opacity: function(i) {
                            var t = 0;
                            if (e.test(i.filter || "")) t = parseFloat(RegExp.$1) / 100;
                            return t
                        }
                    };
                    return function(s, e) {
                        var n = s.currentStyle,
                            r = i[e];
                        if (r) return r(n);
                        else return n[t.__getStyleName(e)] || ""
                    }
                } ();
                t.__setStyleValue = function() {
                    var e = {
                        opacity: function(t, e) {
                            t.style.filter = "alpha(opacity=" + 100 * e + ")"
                        }
                    };
                    return function(i, n, r) {
                        var s = e[n];
                        if (s) s(i, r);
                        else i.style[t.__getStyleName(n)] = r
                    }
                } ();
                t.__appendCSSText = function(n, r) {
                    var t = n.styleSheet,
                        e = t.rules.length,
                        i = r.split(/[\{\}]/);
                    t.addRule(i[0], i[1], e);
                    return t.rules[e]
                }
            });
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release <= "3.0") I$(53,
            function() {
                t.__getAttribute = t.__getAttribute._$aop(null,
                    function(t) {
                        var e = t.args;
                        if ("maxlength" == e[1] && 2147483647 == t.value) t.value = null
                    })
            });
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release <= "2.0") I$(54,
            function() {
                t.__fullScreen = function(i, t) {
                    var e = i.style;
                    e.width = t.scrollWidth + "px";
                    e.height = t.scrollHeight + "px"
                };
                t.__mask = function() {
                    var e = {};
                    t.__unmask = function(n) {
                        var i = n.id,
                            t = e[i];
                        if (t) {
                            delete e[i];
                            t.parentNode.removeChild(t)
                        }
                    };
                    return function(i) {
                        var r = i.id,
                            t = e[r];
                        if (!t) {
                            t = document.createElement("iframe");
                            t.style.position = "absolute";
                            e[r] = t
                        }
                        var n = t.style,
                            s = i.style;
                        n.top = (parseInt(s.top) || 0) + "px";
                        n.left = (parseInt(s.left) || 0) + "px";
                        n.width = i.offsetWidth + "px";
                        n.height = i.offsetHeight + "px";
                        i.insertAdjacentElement("beforeBegin", t);
                        return t
                    }
                } ()
            });
        if ("gecko" === e._$KERNEL.engine) I$(55,
            function() {
                if (!e._$SUPPORT.css3d) e._$SUPPORT.css3d = "MozPerspective" in document.body.style;
                if (! ("insertAdjacentElement" in document.body)) HTMLElement.prototype.insertAdjacentElement = function(e, t) {
                    if (e && t) switch (e) {
                        case "beforeEnd":
                            this.appendChild(t);
                            return;
                        case "beforeBegin":
                            this.parentNode.insertBefore(t, this);
                            return;
                        case "afterBegin":
                            !this.firstChild ? this.appendChild(t) : this.insertBefore(t, this.firstChild);
                            return;
                        case "afterEnd":
                            !this.nextSibling ? this.parentNode.appendChild(t) : this.parentNode.insertBefore(t, this.nextSibling);
                            return
                    }
                };
                if (! ("innerText" in document.body)) {
                    HTMLElement.prototype["__defineGetter__"]("innerText",
                        function() {
                            return this.textContent
                        });
                    HTMLElement.prototype["__defineSetter__"]("innerText",
                        function(t) {
                            this.textContent = t
                        })
                }
            });
        return t
    },
    41, 33, 4);
I$(2,
    function(c, h, n, o, l, i, t, u, d, f) {
        var e = {},
            r, _ = {},
            a = {},
            s = document.createDocumentFragment();
        if (!document.head) document.head = document.getElementsByTagName("head")[0] || document.body;
        t.dump = function() {
            return {
                pool: _,
                dirty: a,
                fragment: s
            }
        };
        t._$id = e._$id = function(e) {
            e = t._$get(e);
            if (e) {
                var i = e.id ? e.id: "auto-id-" + n._$uniqueID();
                if (! ("id" in e)) _[i] = e;
                e.id = i;
                if (!t._$get(i)) a[i] = e;
                return i
            }
        };
        t._$get = function(t) {
            var e = _["" + t];
            if (e) return e;
            if (!n._$isString(t) && !n._$isNumber(t)) return t;
            var e = document.getElementById(t);
            if (!e) e = i.__getElementById(s, t);
            if (e) delete a[t];
            return e || a[t]
        };
        t._$getChildren = e._$getChildren = function(e, r) {
            e = t._$get(e);
            if (!e) return null;
            var s = i.__getChildren(e);
            if (r) n._$reverseEach(s,
                function(e, i, n) {
                    if (!t._$hasClassName(e, r)) n.splice(i, 1)
                });
            return s
        };
        t._$getByClassName = e._$getByClassName = function(e, n) {
            e = t._$get(e);
            return ! e ? null: i.__getElementsByClassName(e, n.trim())
        };
        t._$getSibling = e._$getSibling = function() {
            var e = function() {
                return ! 0
            };
            return function(r, a) {
                r = t._$get(r);
                if (!r) return null;
                var s = {
                    backward: !1,
                    filter: e
                };
                if (n._$isFunction(a)) s.filter = a;
                else s = n._$fetch(s, a);
                var o = s.backward ? i.__previousSibling: i.__nextSibling;
                for (; (r = o(r)) && !s.filter(r););
                return r
            }
        } ();
        t._$getScrollViewPort = function(e) {
            e = t._$get(e);
            if (e) {
                e = e.parentNode;
                for (; e && !(e.scrollHeight > e.clientHeight);) e = e.parentNode;
                if (e) return e
            }
            var i = document.body.scrollHeight,
                n = document.documentElement.scrollHeight;
            return n >= i ? document.documentElement: document.body
        };
        t._$getPageBox = function() {
            var t = function(e) {
                var t = 0;
                n._$forEach(e,
                    function(e) {
                        if (e) if (!t) t = e;
                        else t = Math.min(t, e)
                    });
                return t
            };
            var e = [{
                main: "scroll",
                sub: ["Top", "Left"],
                func: function(t, e, i) {
                    return Math.max(e["scroll" + t], i["scroll" + t])
                }
            },
                {
                    main: "client",
                    sub: ["Width", "Height"],
                    func: function(e, i, n) {
                        return t([i["client" + e], i["offset" + e], n["client" + e], n["offset" + e]])
                    }
                },
                {
                    main: "scroll",
                    sub: ["Width", "Height"],
                    func: function(t, e, i, n) {
                        return Math.max(n["client" + t], e["scroll" + t], i["scroll" + t])
                    }
                }];
            return function(r) {
                var t = {},
                    i = r || document,
                    s = i.body,
                    a = i.documentElement;
                n._$forEach(e,
                    function(e) {
                        var i = e.main;
                        n._$forEach(e.sub,
                            function(n) {
                                t[i + n] = e.func(n, s, a, t)
                            })
                    });
                return t
            }
        } ();
        t._$getMaxBox = function(i, e) {
            var t = n._$merge({},
                i),
                s = e.width / e.height,
                r = i.width / i.height;
            if (s > r && i.height > e.height) {
                t.height = e.height;
                t.width = t.height * r
            }
            if (r > s && i.width > e.width) {
                t.width = e.width;
                t.height = t.width / r
            }
            return t
        };
        t._$scrollTo = e._$scrollTo = function(i) {
            var e = t._$offset(i);
            window.scrollTo(e.x, e.y)
        };
        t._$align = function() {
            var e = /\s+/;
            var t = {
                left: function() {
                    return 0
                },
                center: function(t, e) {
                    return (t.width - e.width) / 2
                },
                right: function(t, e) {
                    return t.width - e.width
                },
                top: function() {
                    return 0
                },
                middle: function(t, e) {
                    return (t.height - e.height) / 2
                },
                bottom: function(t, e) {
                    return t.height - e.height;
                }
            };
            return function(n, r, a) {
                var i = {},
                    s = (a || "").split(e),
                    o = t[s[1]] || t.middle,
                    _ = t[s[0]] || t.center;
                i.top = o(n, r);
                i.left = _(n, r);
                return i
            }
        } ();
        t._$offset = e._$offset = function() {
            var e = function(t) {
                return t == document.body || t == document.documentElement
            };
            return function(n, a) {
                n = t._$get(n);
                if (!n) return null;
                a = t._$get(a) || null;
                var i = n,
                    o = {
                        x: 0,
                        y: 0
                    },
                    _,
                    r,
                    s;
                for (; i && i != a;) {
                    _ = e(i) || i == n;
                    r = _ ? 0 : i.scrollLeft;
                    s = parseInt(t._$getStyle(i, "borderLeftWidth")) || 0;
                    o.x += i.offsetLeft + s - r;
                    r = _ ? 0 : i.scrollTop;
                    s = parseInt(t._$getStyle(i, "borderTopWidth")) || 0;
                    o.y += i.offsetTop + s - r;
                    i = i.offsetParent
                }
                return o
            }
        } ();
        t._$fullScreen = e._$fullScreen = function(e) {
            e = t._$get(e);
            if (e) i.__fullScreen(e, t._$getPageBox())
        };
        t._$mask = e._$mask = function(e) {
            e = t._$get(e);
            if (e) {
                t._$id(e);
                return i.__mask(e)
            }
            return null
        };
        t._$unmask = e._$unmask = function(e) {
            e = t._$get(e);
            if (e) {
                t._$id(e);
                return i.__unmask(e)
            }
            return null
        };
        t._$create = function() {
            var e = {
                a: {
                    href: "#",
                    hideFocus: !0
                },
                style: {
                    type: "text/css"
                },
                link: {
                    type: "text/css",
                    rel: "stylesheet"
                },
                iframe: {
                    frameBorder: 0
                },
                script: {
                    defer: !0,
                    type: "text/javascript"
                }
            };
            return function(a, o, r) {
                var i = document.createElement(a),
                    _ = e[a.toLowerCase()];
                n._$merge(i, _);
                if (o) i.className = o;
                r = t._$get(r);
                if (r) r.appendChild(i);
                else if (!_) s.appendChild(i);
                return i
            }
        } ();
        t._$createXFrame = function() {
            var e = function() {
                if (location.hostname == document.domain) return "about:blank";
                else return 'javascript:(function(){document.open();document.domain="' + document.domain + '";document.close();})();'
            };
            var i = function(e) {
                e = e.trim();
                if (!e) return t._$create("iframe");
                var i;
                try {
                    i = document.createElement('<iframe name="' + e + '"></iframe>');
                    i.frameBorder = 0
                } catch(n) {
                    i = t._$create("iframe");
                    i.name = e
                }
                return i
            };
            return function(r) {
                r = r || u;
                var s = i(r.name || "");
                if (!r.visible) s.style.display = "none";
                if (n._$isFunction(r.onload)) o._$addEvent(s, "load",
                    function(t) {
                        if (s.src) {
                            o._$clearEvent(s, "load");
                            r.onload(t)
                        }
                    });
                var a = r.parent;
                if (n._$isFunction(a)) try {
                    a(s)
                } catch(c) {} else(t._$get(a) || document.body).appendChild(s);
                var _ = r.src || e();
                window.setTimeout(function() {
                        s.src = _
                    },
                    0);
                return s
            }
        } ();
        t._$remove = e._$remove = function() {
            var i = {
                img: function(t) {
                    t.src = h._$BLANK_IMAGE
                },
                iframe: function(t) {
                    t.src = "about:blank"
                }
            };
            var e = function(t, r) {
                if (r) {
                    if (t.getElementsByTagName) n._$forEach(t.getElementsByTagName(r), e)
                } else {
                    var a = (t.tagName || "").toLowerCase(),
                        s = i[a];
                    if (s) s(t)
                }
            };
            return function(i) {
                i = t._$get(i);
                if (i) {
                    if (!arguments[1]) o._$clearEvent(i);
                    e(i);
                    e(i, "img");
                    e(i, "iframe");
                    if (i.parentNode) i.parentNode.removeChild(i)
                }
            }
        } ();
        t._$removeByEC = e._$removeByEC = function(e) {
            e = t._$get(e);
            if (e) try {
                s.appendChild(e)
            } catch(i) {
                console.error(i)
            }
        };
        t._$clearChildren = e._$clearChildren = function(e) {
            e = t._$get(e);
            if (e) n._$reverseEach(e.childNodes,
                function(e) {
                    t._$remove(e)
                })
        };
        t._$wrapInline = e._$wrapInline = function() {
            var e, i = /\s+/;
            var n = function() {
                if (!e) {
                    e = t._$pushCSSText(".#<uispace>{position:relative;zoom:1;}.#<uispace>-show{position:absolute;top:0;left:100%;cursor:text;white-space:nowrap;overflow:hidden;}");
                    t._$dumpCSSText()
                }
            };
            return function(a, o) {
                a = t._$get(a);
                if (!a) return null;
                n();
                o = o || u;
                var s = a.parentNode;
                if (!t._$hasClassName(s, e)) {
                    s = t._$create("span", e);
                    a.insertAdjacentElement("beforeBegin", s);
                    s.appendChild(a)
                }
                var c = o.nid || "",
                    _ = t._$getByClassName(s, c || e + "-show")[0];
                if (!_) {
                    var r = ((o.clazz || "") + " " + c).trim();
                    r = e + "-show" + (!r ? "": " ") + r;
                    _ = t._$create(o.tag || "span", r);
                    s.appendChild(_)
                }
                var r = o.clazz;
                if (r) {
                    r = (r || "").trim().split(i)[0] + "-parent";
                    t._$addClassName(s, r)
                }
                return _
            }
        } ();
        t._$dataset = e._$dataset = function(a, e, o) {
            var s = t._$id(a);
            if (!s) return null;
            if (n._$isString(e)) return i.__dataset(t._$get(a), e, o);
            if (n._$isObject(e)) {
                var r = {};
                n._$forIn(e,
                    function(i, e) {
                        r[e] = t._$dataset(s, e, i)
                    });
                return r
            }
            if (n._$isArray(e)) {
                var r = {};
                n._$forEach(e,
                    function(e) {
                        r[e] = t._$dataset(s, e)
                    });
                return r
            }
            return null
        };
        t._$attr = e._$attr = function(e, n, r) {
            e = t._$get(e);
            if (!e) return "";
            if (void 0 !== r && e.setAttribute) e.setAttribute(n, r);
            return i.__getAttribute(e, n)
        };
        t._$html2node = function() {
            var e = /<(.*?)(?=\s|>)/i,
                i = {
                    li: "ul",
                    tr: "tbody",
                    td: "tr",
                    th: "tr",
                    option: "select"
                };
            return function(r) {
                var s;
                if (e.test(r)) s = i[(RegExp.$1 || "").toLowerCase()] || "";
                var n = t._$create(s || "div");
                n.innerHTML = r;
                var a = t._$getChildren(n);
                return a.length > 1 ? n: a[0]
            }
        } ();
        t._$dom2xml = e._$dom2xml = function(e) {
            e = t._$get(e);
            return ! e ? "": i.__serializeDOM2XML(e)
        };
        t._$xml2dom = function(t) {
            t = (t || "").trim();
            return ! t ? null: i.__parseDOMFromXML(t)
        };
        t._$dom2object = e._$dom2object = function(e, i) {
            i = i || {};
            e = t._$get(e);
            if (!e) return i;
            var s = e.tagName.toLowerCase(),
                r = t._$getChildren(e);
            if (!r || !r.length) {
                i[s] = e.textContent || e.text || "";
                return i
            }
            var a = {};
            i[s] = a;
            n._$forEach(r,
                function(e) {
                    t._$dom2object(e, a)
                });
            return i
        };
        t._$xml2object = function(e) {
            try {
                return t._$dom2object(t._$xml2dom(e))
            } catch(i) {
                return null
            }
        };
        t._$text2type = function() {
            var e = {
                xml: function(e) {
                    return t._$xml2dom(e)
                },
                json: function(t) {
                    try {
                        return JSON.parse(t)
                    } catch(e) {
                        return null
                    }
                },
                dft: function(t) {
                    return t
                }
            };
            return function(i, t) {
                t = (t || "").toLowerCase();
                return (e[t] || e.dft)(i || "")
            }
        } ();
        t._$style = e._$style = function(e, i) {
            e = t._$get(e);
            if (e) n._$loop(i,
                function(i, n) {
                    t._$setStyle(e, n, i)
                })
        };
        t._$setStyle = e._$setStyle = function(e, n, r) {
            e = t._$get(e);
            if (e) i.__setStyleValue(e, n, i.__processCSSText(r))
        };
        t._$getStyle = e._$getStyle = function(e, n) {
            e = t._$get(e);
            return ! e ? "": i.__getStyleValue(e, n)
        };
        t._$addScript = function(t) {
            try {
                t = t.trim();
                if (t) return new Function(t)()
            } catch(e) {
                console.error(e.message);
                console.error(e.stack)
            }
        };
        t._$addStyle = function() {
            var e = /[\s\r\n]+/gi;
            return function(n) {
                n = (n || "").replace(e, " ").trim();
                var r = null;
                if (n) {
                    r = t._$create("style");
                    document.head.appendChild(r);
                    i.__injectCSSText(r, i.__processCSSText(n))
                }
                return r
            }
        } ();
        t._$pushCSSText = function() {
            var t = /#<(.*?)>/g,
                e = +new Date;
            return function(i, s) {
                if (!r) r = [];
                var e = "auto-" + n._$uniqueID(),
                    a = n._$merge({
                            uispace: e
                        },
                        s);
                r.push(i.replace(t,
                    function(t, e) {
                        return a[e] || t
                    }));
                return e
            }
        } ();
        t._$dumpCSSText = function() {
            if (r) {
                t._$addStyle(r.join(" "));
                r = null
            }
        };
        t._$appendCSSText = e._$appendCSSText = function(e, n) {
            e = t._$get(e);
            return ! e ? null: i.__appendCSSText(e, i.__processCSSText(n))
        };
        t._$addClassName = e._$addClassName = function(e, n) {
            e = t._$get(e);
            if (e) i.__processClassName(e, "add", n)
        };
        t._$delClassName = e._$delClassName = function(e, n) {
            e = t._$get(e);
            if (e) i.__processClassName(e, "remove", n)
        };
        t._$replaceClassName = e._$replaceClassName = function(e, n, r) {
            e = t._$get(e);
            if (e) i.__processClassName(e, "replace", n, r)
        };
        t._$hasClassName = e._$hasClassName = function(e, n) {
            e = t._$get(e);
            if (e) return i.__hasClassName(e, n);
            else return ! 1
        };
        t._$matrix = function(t) {
            t = (t || "").trim();
            return i.__getCSSMatrix(t)
        };
        t._$css3d = e._$css3d = function(e, r, s) {
            e = t._$get(e);
            if (e) {
                var n = i.__processTransformValue(r, s);
                if (n) t._$setStyle(e, "transform", n)
            }
        };
        l._$merge(e);
        if (!0) c.copy(c.P("nej.e"), t);
        return t
    },
    15, 19, 4, 3, 17, 20);
I$(5,
    function(r, a, n, t, i, _, s, o) {
        var e;
        i._$$EventTarget = a._$klass();
        e = i._$$EventTarget.prototype;
        i._$$EventTarget._$allocate = function(t) {
            t = t || {};
            var e = !!this.__pool && this.__pool.shift();
            if (!e) {
                e = new this(t);
                this.__inst__ = (this.__inst__ || 0) + 1
            }
            e.__reset(t);
            return e
        };
        i._$$EventTarget._$recycle = function() {
            var e = function(t, e, i) {
                t._$recycle();
                i.splice(e, 1)
            };
            return function(i) {
                if (!i) return null;
                if (!t._$isArray(i)) {
                    if (! (i instanceof this)) {
                        var n = i.constructor;
                        if (n._$recycle) n._$recycle(i);
                        return null
                    }
                    if (i == this.__instance) delete this.__instance;
                    if (i == this.__inctanse) delete this.__inctanse;
                    i.__destroy();
                    if (!this.__pool) this.__pool = [];
                    if (t._$indexOf(this.__pool, i) < 0) this.__pool.push(i);
                    return null
                }
                t._$reverseEach(i, e, this)
            }
        } ();
        i._$$EventTarget._$getInstance = function(t) {
            if (!this.__instance) this.__instance = this._$allocate(t);
            return this.__instance
        };
        i._$$EventTarget._$getInstanceWithReset = function(t, e) {
            if (e && this.__inctanse) {
                this.__inctanse._$recycle();
                delete this.__inctanse
            }
            if (!this.__inctanse) this.__inctanse = this._$allocate(t);
            else this.__inctanse.__reset(t);
            return this.__inctanse
        };
        e.__init = function() {
            this.__events = {};
            this.__events_dom = {};
            this.id = t._$uniqueID()
        };
        e.__reset = function(t) {
            this._$batEvent(t)
        };
        e.__destroy = function() {
            this._$clearEvent();
            this.__doClearDomEvent()
        };
        e.__doInitDomEvent = function() {
            var e = function(e) {
                if (e && !(e.length < 3)) {
                    this.__events_dom["de-" + t._$uniqueID()] = e;
                    n._$addEvent.apply(n, e)
                }
            };
            return function(i) {
                t._$forEach(i, e, this)
            }
        } ();
        e.__doClearDomEvent = function() {
            var e = function(t, e, i) {
                delete i[e];
                n._$delEvent.apply(n, t)
            };
            return function() {
                t._$loop(this.__events_dom, e)
            }
        } ();
        e.__doClearComponent = function(e) {
            e = e || s;
            t._$loop(this,
                function(t, i, n) {
                    if (t && t._$recycle && !e(t)) {
                        delete n[i];
                        t._$recycle()
                    }
                })
        };
        e._$recycle = function() {
            this.constructor._$recycle(this)
        };
        e._$hasEvent = function(t) {
            var t = (t || "").toLowerCase(),
                e = this.__events[t];
            return !! e && e !== s
        };
        e._$delEvent = function(e, n) {
            var e = (e || "").toLowerCase(),
                i = this.__events[e];
            if (t._$isArray(i)) {
                t._$reverseEach(i,
                    function(t, e, i) {
                        if (t == n) i.splice(e, 1)
                    });
                if (!i.length) delete this.__events[e]
            } else if (i == n) delete this.__events[e]
        };
        e._$setEvent = function(e, i) {
            if (e && t._$isFunction(i)) this.__events[e.toLowerCase()] = i
        };
        e._$batEvent = function() {
            var e = function(t, e) {
                this._$setEvent(e, t)
            };
            return function(i) {
                t._$loop(i, e, this)
            }
        } ();
        e._$clearEvent = function() {
            var e = function(e, t) {
                this._$clearEvent(t)
            };
            return function(i) {
                var i = (i || "").toLowerCase();
                if (i) delete this.__events[i];
                else t._$loop(this.__events, e, this)
            }
        } ();
        e._$addEvent = function(e, i) {
            if (e && t._$isFunction(i)) {
                e = e.toLowerCase();
                var n = this.__events[e];
                if (n) {
                    if (!t._$isArray(n)) this.__events[e] = [n];
                    this.__events[e].push(i)
                } else this.__events[e] = i
            }
        };
        e._$dispatchEvent = function(n) {
            var n = (n || "").toLowerCase(),
                e = this.__events[n];
            if (e) {
                var i = o.slice.call(arguments, 1);
                if (t._$isArray(e)) t._$forEach(e,
                    function(t) {
                        if (!1) t.apply(this, i);
                        else try {
                            t.apply(this, i)
                        } catch(e) {
                            console.error(e.message);
                            console.error(e.stack)
                        }
                    },
                    this);
                else e.apply(this, i)
            }
        };
        if (!0) {
            i._$$Event = i._$$EventTarget;
            r.copy(r.P("nej.ut"), i)
        }
        return i
    },
    15, 1, 3, 4); !
    function() {
        if ("undefined" == typeof TrimPath) {
            TrimPath = {};
            if ("undefined" != typeof exports) TrimPath = exports
        }
        var e = {},
            s = [],
            u = /\s+/g,
            c = +new Date,
            n,
            i,
            r;
        var t = function() {
            var t = /^\s*[\[\{'"].*?[\]\}'"]\s*$/,
                e = /[\&\|\<\>\+\-\*\/\%\,\(\)\[\]\?\:\!\=\;]/,
                i = /^(?:defined|null|undefined|true|false|instanceof|new|this|typeof|\$v|[\d]+)$/i,
                n = /^new\s+/,
                s = /['"]/;
            var a = function(e) {
                if (!t.test(e)) {
                    e = e.split(".")[0].trim();
                    if (e && !s.test(e)) {
                        e = e.replace(n, "");
                        try {
                            if (i.test(e)) return;
                            r[e] = 1
                        } catch(a) {}
                    }
                }
            };
            return function(i) {
                i = i || "";
                if (i && !t.test(i)) {
                    var r = i.split(e);
                    for (var n = 0,
                             s = r.length; s > n; n++) a(r[n])
                }
            }
        } ();
        var d = function(e) {
            if ("in" != e[2]) throw "bad for loop statement: " + e.join(" ");
            s.push(e[1]);
            t(e[3]);
            return "var __HASH__" + e[1] + " = " + e[3] + "," + e[1] + "," + e[1] + "_count=0;if (!!__HASH__" + e[1] + ")for(var " + e[1] + "_key in __HASH__" + e[1] + "){" + e[1] + " = __HASH__" + e[1] + "[" + e[1] + "_key];if (typeof(" + e[1] + ')=="function") continue;' + e[1] + "_count++;"
        };
        var $ = function() {
            var t = s[s.length - 1];
            return "}; if(!__HASH__" + t + "||!" + t + "_count){"
        };
        var N = function() {
            s.pop();
            return "};"
        };
        var y = function(e) {
            if ("as" != e[2]) throw "bad for list loop statement: " + e.join(" ");
            var i = e[1].split("..");
            if (i.length > 1) {
                t(i[0]);
                t(i[1]);
                return "for(var " + e[3] + "," + e[3] + "_index=0," + e[3] + "_beg=" + i[0] + "," + e[3] + "_end=" + i[1] + "," + e[3] + "_length=parseInt(" + e[3] + "_end-" + e[3] + "_beg+1);" + e[3] + "_index<" + e[3] + "_length;" + e[3] + "_index++){" + e[3] + " = " + e[3] + "_beg+" + e[3] + "_index;"
            } else {
                t(e[1]);
                return "for(var __LIST__" + e[3] + " = " + e[1] + "," + e[3] + "," + e[3] + "_index=0," + e[3] + "_length=__LIST__" + e[3] + ".length;" + e[3] + "_index<" + e[3] + "_length;" + e[3] + "_index++){" + e[3] + " = __LIST__" + e[3] + "[" + e[3] + "_index];"
            }
        };
        var E = function(t) {
            if (t && t.length) {
                t.shift();
                var e = t[0].split("(")[0];
                return "var " + e + " = function" + t.join("").replace(e, "") + "{var __OUT=[];"
            }
        };
        var f = function(t) {
            if (!t[1]) throw "bad include statement: " + t.join(" ");
            return 'if (typeof inline == "function"){__OUT.push(inline('
        };
        var _ = function(e, i) {
            t(i.slice(1).join(" "));
            return e
        };
        var p = function(t) {
            return _("if(", t)
        };
        var m = function(t) {
            return _("}else if(", t)
        };
        var v = function(t) {
            return _("var ", t)
        };
        i = {
            blk: /^\{(cdata|minify|eval)/i,
            tag: "forelse|for|list|if|elseif|else|var|macro|break|notrim|trim|include",
            def: {
                "if": {
                    pfix: p,
                    sfix: "){",
                    pmin: 1
                },
                "else": {
                    pfix: "}else{"
                },
                elseif: {
                    pfix: m,
                    sfix: "){",
                    pdft: "true"
                },
                "/if": {
                    pfix: "}"
                },
                "for": {
                    pfix: d,
                    pmin: 3
                },
                forelse: {
                    pfix: $
                },
                "/for": {
                    pfix: N
                },
                list: {
                    pfix: y,
                    pmin: 3
                },
                "/list": {
                    pfix: "};"
                },
                "break": {
                    pfix: "break;"
                },
                "var": {
                    pfix: v,
                    sfix: ";"
                },
                macro: {
                    pfix: E
                },
                "/macro": {
                    pfix: 'return __OUT.join("");};'
                },
                trim: {
                    pfix: function() {
                        n = !0
                    }
                },
                "/trim": {
                    pfix: function() {
                        n = null
                    }
                },
                inline: {
                    pfix: f,
                    pmin: 1,
                    sfix: "));}"
                }
            },
            ext: {
                seed: function(t) {
                    return (t || "") + "" + c
                },
                "default": function(t, e) {
                    return t || e
                }
            }
        };
        var g = function() {
            var t = /\\([\{\}])/g;
            return function(s, n) {
                s = s.replace(t, "$1");
                var r = s.slice(1, -1).split(u),
                    e = i.def[r[0]];
                if (e) {
                    if (e.pmin && e.pmin >= r.length) throw "Statement needs more parameters:" + s;
                    n.push(e.pfix && "string" != typeof e.pfix ? e.pfix(r) : e.pfix || "");
                    if (e.sfix) {
                        if (r.length <= 1) {
                            if (e.pdft) n.push(e.pdft)
                        } else for (var o = 1,
                                        _ = r.length; _ > o; o++) {
                            if (o > 1) n.push(" ");
                            n.push(r[o])
                        }
                        n.push(e.sfix)
                    }
                } else a(s, n)
            }
        } ();
        var h = function(e, i) {
            if (e && e.length) if (1 != e.length) {
                var n = e.pop().split(":");
                i.push("__MDF['" + n.shift() + "'](");
                h(e, i);
                if (n.length > 0) {
                    var s = n.join(":");
                    t(s);
                    i.push("," + s)
                }
                i.push(")")
            } else {
                var r = e.pop();
                t(r);
                i.push("" == r ? '""': r)
            }
        };
        var a = function(r, s) {
            if (r) {
                var e = r.split("\n");
                if (e && e.length) for (var i = 0,
                                            a = e.length,
                                            t; a > i; i++) {
                    t = e[i];
                    if (n) {
                        t = t.trim();
                        if (!t) continue
                    }
                    b(t, s);
                    if (n && a - 1 > i) s.push("__OUT.push('\\n');")
                }
            }
        };
        var b = function() {
            var t = /\|\|/g,
                e = /#@@#/g;
            return function(i, _) {
                var r = "}",
                    s = -1,
                    d = i.length,
                    a, u, n, l, c;
                for (; s + r.length < d;) {
                    a = "${";
                    u = "}";
                    n = i.indexOf(a, s + r.length);
                    if (0 > n) break;
                    if ("%" == i.charAt(n + 2)) {
                        a = "${%";
                        u = "%}"
                    }
                    l = i.indexOf(u, n + a.length);
                    if (0 > l) break;
                    o(i.substring(s + r.length, n), _);
                    c = i.substring(n + a.length, l).replace(t, "#@@#").split("|");
                    for (var f = 0,
                             p = c.length; p > f; c[f] = c[f].replace(e, "||"), f++);
                    _.push("__OUT.push(");
                    h(c, _);
                    _.push(");");
                    r = u;
                    s = l
                }
                o(i.substring(s + r.length), _)
            }
        } ();
        var o = function() {
            var t = {
                r: /\n|\\|\'/g,
                "\n": "\\n",
                "\\": "\\\\",
                "'": "\\'"
            };
            var e = function(e) {
                return (e || "").replace(t.r,
                    function(e) {
                        return t[e] || e
                    })
            };
            return function(t, i) {
                if (t) i.push("__OUT.push('" + e(t) + "');")
            }
        } ();
        var l = function() {
            var e = /\t/g,
                n = /\n/g,
                s = /\r\n?/g;
            var t = function(e, i) {
                var t = e.indexOf("}", i + 1);
                for (;
                    "\\" == e.charAt(t - 1);) t = e.indexOf("}", t + 1);
                return t
            };
            var _ = function() {
                var e = [],
                    i = arguments[0];
                for (var t in i) {
                    t = (t || "").trim();
                    if (t) e.push(t + "=$v('" + t + "')");
                    else;
                }
                return e.length > 0 ? "var " + e.join(",") + ";": ""
            };
            return function(h) {
                r = {};
                h = h.replace(s, "\n").replace(e, "    ");
                var l = ["if(!__CTX) return '';", ""];
                l.push("function $v(__NAME){var v = __CTX[__NAME];return v==null?window[__NAME]:v;};");
                l.push("var defined=function(__NAME){return __CTX[__NAME]!=null;},");
                l.push("__OUT=[];");
                var d = -1,
                    y = h.length;
                var c, p, N, v, f, $, b, m;
                for (; y > d + 1;) {
                    c = d;
                    c = h.indexOf("{", c + 1);
                    for (; c >= 0;) {
                        p = t(h, c);
                        N = h.substring(c, p);
                        v = N.match(i.blk);
                        if (v) {
                            f = v[1].length + 1;
                            $ = h.indexOf("}", c + f);
                            if ($ >= 0) {
                                b = 0 >= $ - c - f ? "{/" + v[1] + "}": N.substr(f + 1);
                                f = h.indexOf(b, $ + 1);
                                if (f >= 0) {
                                    a(h.substring(d + 1, c), l);
                                    m = h.substring($ + 1, f);
                                    switch (v[1]) {
                                        case "cdata":
                                            o(m, l);
                                            break;
                                        case "minify":
                                            o(m.replace(n, " ").replace(u, " "), l);
                                            break;
                                        case "eval":
                                            if (m) l.push("__OUT.push((function(){" + m + "})());")
                                    }
                                    c = d = f + b.length - 1
                                }
                            }
                        } else if ("$" != h.charAt(c - 1) && "\\" != h.charAt(c - 1) && 0 == N.substr("/" == N.charAt(1) ? 2 : 1).search(i.tag)) break;
                        c = h.indexOf("{", c + 1)
                    }
                    if (0 > c) break;
                    p = t(h, c);
                    if (0 > p) break;
                    a(h.substring(d + 1, c), l);
                    g(h.substring(c, p + 1), l);
                    d = p
                }
                a(h.substring(d + 1), l);
                l.push(';return __OUT.join("");');
                l[1] = _(r);
                r = null;
                return new Function("__CTX", "__MDF", l.join(""))
            }
        } ();
        TrimPath.seed = function() {
            return c
        };
        TrimPath.merge = function() {
            var t = {};
            TrimPath.dump = function() {
                return {
                    func: t,
                    text: e
                }
            };
            return function(n, s, r) {
                try {
                    s = s || {};
                    if (!t[n] && !e[n]) return "";
                    if (!t[n]) {
                        t[n] = l(e[n]);
                        delete e[n]
                    }
                    if (r) for (var a in i.ext) if (!r[a]) r[a] = i.ext[a];
                    return t[n](s, r || i.ext)
                } catch(o) {
                    return o.message || ""
                }
            }
        } ();
        TrimPath.parse = function() {
            var t = +new Date;
            return function(n, i) {
                if (!n) return "";
                i = i || "ck-" + t++;
                if (null != e[i]) {
                    console.warn("jst template overwrited with key " + i);
                    console.debug("old template content: " + e[i].replace(/\n/g, " "));
                    console.debug("new template content: " + n.replace(/\n/g, " "))
                }
                e[i] = n;
                return i
            }
        } ()
    } ();
I$(22,
    function(a, i, n, s, o, t, _, c, u) {
        var r = {};
        t._$seed = TrimPath.seed;
        t._$get = function() {
            var e = function(e) {
                return ! t._$getTextTemplate ? "": t._$getTextTemplate(e)
            };
            return function(s, n, t) {
                n = n || {};
                n.inline = e;
                t = i._$merge({},
                    r, t);
                t.rand = i._$uniqueID;
                t.format = i._$format;
                t.escape = i._$escape;
                t.inline = e;
                return TrimPath.merge(s, n, t)
            }
        } ();
        t._$add = function(e, r) {
            if (!e) return "";
            var i, t = n._$get(e);
            if (t) {
                i = t.id;
                e = t.value || t.innerText;
                if (!r) n._$remove(t)
            }
            return TrimPath.parse(e, i)
        };
        t._$addTemplate = function(t, e) {
            return TrimPath.parse(t, e)
        };
        t._$render = function(e, i, r, s) {
            e = n._$get(e);
            if (e) e.innerHTML = t._$get(i, r, s)
        };
        t._$extend = function(t) {
            i._$merge(r, t)
        };
        s._$merge({
            _$render: t._$render
        });
        if (!0) {
            var e = a.P("nej.e");
            e._$addHtmlTemplate = t._$add;
            e._$getHtmlTemplate = t._$get;
            e._$getHtmlTemplateSeed = t._$seed;
            e._$renderHtmlTemplate = t._$render;
            e._$registJSTExt = t._$extend
        }
        return t
    },
    15, 4, 2, 17, 56);
I$(34,
    function(n, a, s, i, e, c, r, o, _, u) {
        var t;
        r._$$CustomEvent = a._$klass();
        t = r._$$CustomEvent._$extend(c._$$EventTarget);
        t.__init = function() {
            this.__cache = {};
            this.__super()
        };
        t.__reset = function(t) {
            this.__super(t);
            this.__element = s._$get(t.element) || window;
            this.__doEventInit(t.event);
            this.__doEventAPIEnhance();
            this._$dispatchEvent("oninit")
        };
        t.__destroy = function() {
            var t = function(i, t, n) {
                if (!e._$isArray(i)) e._$safeDelete(this.__element, t);
                delete n[t]
            };
            return function() {
                this.__super();
                e._$loop(this.__cache, t, this);
                delete this.__element
            }
        } ();
        t.__isDelegate = function(t, e) {
            t = s._$get(t);
            return ! (t !== this.__element || e && !this.__cache["on" + e])
        };
        t.__doEventInit = function(t) {
            if (!e._$isString(t)) {
                if (e._$isArray(t)) e._$forEach(t, this.__doEventInit, this)
            } else {
                var i = "on" + t;
                if (!this.__cache[i]) this.__cache[i] = this.__doEventDispatch._$bind(this, t);
                this.__doEventBind(t)
            }
        };
        t.__doEventBind = function(e) {
            var i = "on" + e,
                t = this.__element[i],
                n = this.__cache[i];
            if (t != n) {
                this.__doEventDelete(e);
                if (t && t != _) this.__doEventAdd(e, t);
                this.__element[i] = n
            }
        };
        t.__doEventAdd = function(n, i, r) {
            var t = this.__cache[n];
            if (!t) {
                t = [];
                this.__cache[n] = t
            }
            if (e._$isFunction(i)) ! r ? t.push(i) : t.unshift(i)
        };
        t.__doEventDelete = function(i, n) {
            var t = this.__cache[i];
            if (t && t.length) if (n) e._$reverseEach(t,
                function(t, e, i) {
                    if (n === t) {
                        i.splice(e, 1);
                        return ! 0
                    }
                });
            else delete this.__cache[i]
        };
        t.__doEventDispatch = function(i, t) {
            t = t || {
                    noargs: !0
                };
            if (t == o) t = {};
            t.type = i;
            this._$dispatchEvent("ondispatch", t);
            if (!t.stopped) e._$forEach(this.__cache[i],
                function(e) {
                    if (!1) e(t);
                    else try {
                        e(t)
                    } catch(i) {
                        console.error(i.message);
                        console.error(i.stack)
                    }
                })
        };
        t.__doEventAPIEnhance = function() {
            var t = function(i) {
                var t = i.args,
                    e = t[1].toLowerCase();
                if (this.__isDelegate(t[0], e)) {
                    i.stopped = !0;
                    this.__doEventBind(e);
                    this.__doEventAdd(e, t[2], t[3]);
                    this._$dispatchEvent("oneventadd", {
                        type: e,
                        listener: t[2]
                    })
                }
            };
            var r = function(e) {
                var t = e.args,
                    i = t[1].toLowerCase();
                if (this.__isDelegate(t[0], i)) {
                    e.stopped = !0;
                    this.__doEventDelete(i, t[2])
                }
            };
            var s = function(n) {
                var t = n.args,
                    i = (t[1] || "").toLowerCase();
                if (this.__isDelegate(t[0])) {
                    if (i) {
                        this.__doEventDelete(i);
                        return
                    }
                    e._$loop(this.__cache,
                        function(t, i) {
                            if (e._$isArray(t)) this.__doEventDelete(i)
                        },
                        this)
                }
            };
            var a = function(e) {
                var t = e.args,
                    i = t[1].toLowerCase();
                if (this.__isDelegate(t[0], i)) {
                    e.stopped = !0;
                    t[0]["on" + i].apply(t[0], t.slice(2))
                }
            };
            return function() {
                if (!this.__enhanced) {
                    this.__enhanced = !0;
                    i._$addEvent = i._$addEvent._$aop(t._$bind(this));
                    i._$delEvent = i._$delEvent._$aop(r._$bind(this));
                    i._$clearEvent = i._$clearEvent._$aop(s._$bind(this));
                    i._$dispatchEvent = i._$dispatchEvent._$aop(a._$bind(this));
                    if (!0) n.copy(n.P("nej.v"), i)
                }
            }
        } ();
        if (!0) n.copy(n.P("nej.ut"), r);
        return r
    },
    15, 1, 2, 3, 4, 5);
I$(37,
    function(h, a, e, n, r, s, i, u, _, c) {
        var t, o = 6e4;
        i._$$LoaderAbstract = a._$klass();
        t = i._$$LoaderAbstract._$extend(s._$$EventTarget);
        t.__init = function() {
            this.__super();
            this.__qopt = {
                onerror: this.__onQueueError._$bind(this),
                onload: this.__onQueueLoaded._$bind(this)
            };
            if (!this.constructor.__cache) this.constructor.__cache = {
                loaded: {}
            }
        };
        t.__reset = function(t) {
            this.__super(t);
            this.__version = t.version;
            this.__timeout = t.timeout;
            this.__qopt.version = this.__version;
            this.__qopt.timeout = this.__timeout
        };
        t.__delLoadData = function(t) {
            delete this.constructor.__cache[t]
        };
        t.__getLoadData = function(t) {
            return this.constructor.__cache[t]
        };
        t.__setLoadData = function(t, e) {
            this.constructor.__cache[t] = e;
        };
        t.__getRequest = _;
        t.__doClearRequest = function(t) {
            n._$clearEvent(t)
        };
        t.__doRequest = function(t) {
            t.src = this.__url;
            document.head.appendChild(t)
        };
        t.__doClear = function() {
            var t = this.__getLoadData(this.__url);
            if (t) {
                window.clearTimeout(t.timer);
                this.__doClearRequest(t.request);
                delete t.bind;
                delete t.timer;
                delete t.request;
                this.__delLoadData(this.__url);
                this.__getLoadData("loaded")[this.__url] = !0
            }
        };
        t.__doCallback = function(r) {
            var n = this.__getLoadData(this.__url);
            if (n) {
                var t = n.bind;
                this.__doClear();
                if (t && t.length > 0) {
                    var e;
                    for (; t.length;) {
                        e = t.shift();
                        try {
                            e._$dispatchEvent(r, arguments[1])
                        } catch(i) {
                            if (!1) throw i;
                            console.error(i.message);
                            console.error(i.stack)
                        }
                        e._$recycle()
                    }
                }
            }
        };
        t.__onError = function(t) {
            this.__doCallback("onerror", t)
        };
        t.__onLoaded = function() {
            this.__doCallback("onload")
        };
        t.__doLoadQueue = function(t) {
            this.constructor._$allocate(this.__qopt)._$load(t)
        };
        t.__onQueueCheck = function(e) {
            var t = this.__getLoadData(this.__key);
            if (t) {
                if (e) t.error++;
                t.loaded++;
                if (! (t.loaded < t.total)) {
                    this.__delLoadData(this.__key);
                    this._$dispatchEvent(t.error > 0 ? "onerror": "onload")
                }
            }
        };
        t.__onQueueError = function(t) {
            this.__onQueueCheck(!0)
        };
        t.__onQueueLoaded = function() {
            this.__onQueueCheck()
        };
        t._$load = function(s) {
            s = r._$absolute(s);
            if (s) {
                this.__url = s;
                if (this.__version) this.__url += (this.__url.indexOf("?") < 0 ? "?": "&") + this.__version;
                if (!this.__getLoadData("loaded")[this.__url]) {
                    var t = this.__getLoadData(this.__url),
                        i;
                    if (t) {
                        t.bind.unshift(this);
                        t.timer = window.clearTimeout(t.timer)
                    } else {
                        i = this.__getRequest();
                        t = {
                            request: i,
                            bind: [this]
                        };
                        this.__setLoadData(this.__url, t);
                        n._$addEvent(i, "load", this.__onLoaded._$bind(this));
                        n._$addEvent(i, "error", this.__onError._$bind(this, {
                            code: e._$CODE_ERRSERV,
                            message: "无法加载指定资源文件[" + this.__url + "]！"
                        }))
                    }
                    if (0 != this.__timeout) t.timer = window.setTimeout(this.__onError._$bind(this, {
                        code: e._$CODE_TIMEOUT,
                        message: "指定资源文件[" + this.__url + "]载入超时！"
                    }), this.__timeout || o);
                    if (i) this.__doRequest(i);
                    this._$dispatchEvent("onloading")
                } else {
                    try {
                        this._$dispatchEvent("onload")
                    } catch(a) {
                        if (!1) throw a;
                        console.error(a.message);
                        console.error(a.stack)
                    }
                    this._$recycle()
                }
            } else this._$dispatchEvent("onerror", {
                code: e._$CODE_NOTASGN,
                message: "请指定要载入的资源地址！"
            })
        };
        t._$queue = function(t) {
            if (t && t.length) {
                this.__key = r._$uniqueID();
                var i = {
                    error: 0,
                    loaded: 0,
                    total: t.length
                };
                this.__setLoadData(this.__key, i);
                r._$forEach(t,
                    function(t, e) {
                        if (t) this.__doLoadQueue(t);
                        else i.total--
                    },
                    this);
                this._$dispatchEvent("onloading")
            } else this._$dispatchEvent("onerror", {
                code: e._$CODE_NOTASGN,
                message: "请指定要载入的资源队列！"
            })
        };
        return i
    },
    15, 1, 19, 3, 4, 5);
I$(29,
    function(e, i, t, n, r, s) {
        t._$cookie = function() {
            var t = new Date,
                e = +t,
                r = 864e5;
            var s = function(r) {
                var t = document.cookie,
                    n = "\\b" + r + "=",
                    e = t.search(n);
                if (0 > e) return "";
                e += n.length - 2;
                var i = t.indexOf(";", e);
                if (0 > i) i = t.length;
                return t.substring(e, i) || ""
            };
            return function(o, a) {
                if (void 0 === a) return s(o);
                if (i._$isString(a)) {
                    if (a) {
                        document.cookie = o + "=" + a + ";";
                        return a
                    }
                    a = {
                        expires: -100
                    }
                }
                a = a || n;
                var _ = o + "=" + (a.value || "") + ";";
                delete a.value;
                if (void 0 !== a.expires) {
                    t.setTime(e + a.expires * r);
                    a.expires = t.toGMTString()
                }
                _ += i._$object2string(a, ";");
                document.cookie = _
            }
        } ();
        if (!0) e.copy(e.P("nej.j"), t);
        return t
    },
    15, 4); !
    function() {
        var e = !0,
            t = null; !
            function(N) {
                function d(n) {
                    if ("bug-string-char-index" == n) return "a" != "a" [0];
                    var s, u = "json" == n;
                    if (u || "json-stringify" == n || "json-parse" == n) {
                        if ("json-stringify" == n || u) {
                            var i = a.stringify,
                                h = "function" == typeof i && o;
                            if (h) { (s = function() {
                                return 1
                            }).toJSON = s;
                                try {
                                    h = "0" === i(0) && "0" === i(new Number) && '""' == i(new String) && i(r) === c && i(c) === c && i() === c && "1" === i(s) && "[1]" == i([s]) && "[null]" == i([c]) && "null" == i(t) && "[null,null,null]" == i([c, r, t]) && '{"a":[1,true,false,null,"\\u0000\\b\\n\\f\\r\\t"]}' == i({
                                            a: [s, e, !1, t, "\x00\b\n\f\r	"]
                                        }) && "1" === i(t, s) && "[\n 1,\n 2\n]" == i([1, 2], t, 1) && '"-271821-04-20T00:00:00.000Z"' == i(new Date( - 864e13)) && '"+275760-09-13T00:00:00.000Z"' == i(new Date(864e13)) && '"-000001-01-01T00:00:00.000Z"' == i(new Date( - 621987552e5)) && '"1969-12-31T23:59:59.999Z"' == i(new Date( - 1))
                                } catch(l) {
                                    h = !1
                                }
                            }
                            if (!u) return h
                        }
                        if ("json-parse" == n || u) {
                            n = a.parse;
                            if ("function" == typeof n) try {
                                if (0 === n("0") && !n(!1)) {
                                    s = n('{"a":[1,true,false,null,"\\u0000\\b\\n\\f\\r\\t"]}');
                                    var _ = 5 == s.a.length && 1 === s.a[0];
                                    if (_) {
                                        try {
                                            _ = !n('"	"')
                                        } catch(f) {}
                                        if (_) try {
                                            _ = 1 !== n("01")
                                        } catch(d) {}
                                    }
                                }
                            } catch(p) {
                                _ = !1
                            }
                            if (!u) return _
                        }
                        return h && _
                    }
                }
                var r = {}.toString,
                    s, $, c, v = "function" == typeof define && define.amd,
                    a = "object" == typeof exports && exports;
                a || v ? "object" == typeof JSON && JSON ? a ? (a.stringify = JSON.stringify, a.parse = JSON.parse) : a = JSON: v && (a = N.JSON = {}) : a = N.JSON || (N.JSON = {});
                var o = new Date( - 0xc782b5b800cec);
                try {
                    o = -109252 == o.getUTCFullYear() && 0 === o.getUTCMonth() && 1 === o.getUTCDate() && 10 == o.getUTCHours() && 37 == o.getUTCMinutes() && 6 == o.getUTCSeconds() && 708 == o.getUTCMilliseconds()
                } catch(k) {}
                if (!d("json")) {
                    var l = d("bug-string-char-index");
                    if (!o) var _ = Math.floor,
                        x = [0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334],
                        f = function(e, t) {
                            return x[t] + 365 * (e - 1970) + _((e - 1969 + (t = +(t > 1))) / 4) - _((e - 1901 + t) / 100) + _((e - 1601 + t) / 400)
                        };
                    if (! (s = {}.hasOwnProperty)) s = function(n) {
                        var e = {},
                            i;
                        if ((e.__proto__ = t, e.__proto__ = {
                                toString: 1
                            },
                                e).toString != r) s = function(e) {
                            var i = this.__proto__,
                                e = e in (this.__proto__ = t, this);
                            this.__proto__ = i;
                            return e
                        };
                        else {
                            i = e.constructor;
                            s = function(t) {
                                var e = (this.constructor || i).prototype;
                                return t in this && !(t in e && this[t] === e[t])
                            }
                        }
                        e = t;
                        return s.call(this, n)
                    };
                    var S = {
                        "boolean": 1,
                        number: 1,
                        string: 1,
                        undefined: 1
                    };
                    $ = function(o, _) {
                        var i = 0,
                            n, e, a; (n = function() {
                            this.valueOf = 0
                        }).prototype.valueOf = 0;
                        e = new n;
                        for (a in e) s.call(e, a) && i++;
                        n = e = t;
                        if (i) i = 2 == i ?
                            function(e, n) {
                                var i = {},
                                    a = "[object Function]" == r.call(e),
                                    t;
                                for (t in e) ! (a && "prototype" == t) && !s.call(i, t) && (i[t] = 1) && s.call(e, t) && n(t)
                            }: function(e, i) {
                            var a = "[object Function]" == r.call(e),
                                t,
                                n;
                            for (t in e) ! (a && "prototype" == t) && s.call(e, t) && !(n = "constructor" === t) && i(t); (n || s.call(e, t = "constructor")) && i(t)
                        };
                        else {
                            e = ["valueOf", "toString", "toLocaleString", "propertyIsEnumerable", "isPrototypeOf", "hasOwnProperty", "constructor"];
                            i = function(i, o) {
                                var a = "[object Function]" == r.call(i),
                                    n,
                                    t;
                                if (t = !a) if (t = "function" != typeof i.constructor) {
                                    t = typeof i.hasOwnProperty;
                                    t = "object" == t ? !!i.hasOwnProperty: !S[t]
                                }
                                t = t ? i.hasOwnProperty: s;
                                for (n in i) ! (a && "prototype" == n) && t.call(i, n) && o(n);
                                for (a = e.length; n = e[--a]; t.call(i, n) && o(n));
                            }
                        }
                        i(o, _)
                    };
                    if (!d("json-stringify")) {
                        var w = {
                                92 : "\\\\",
                                34 : '\\"',
                                8 : "\\b",
                                12 : "\\f",
                                10 : "\\n",
                                13 : "\\r",
                                9 : "\\t"
                            },
                            u = function(t, e) {
                                return ("000000" + (e || 0)).slice( - t)
                            },
                            y = function(e) {
                                var i = '"',
                                    t = 0,
                                    r = e.length,
                                    s = r > 10 && l,
                                    a;
                                for (s && (a = e.split("")); r > t; t++) {
                                    var n = e.charCodeAt(t);
                                    switch (n) {
                                        case 8:
                                        case 9:
                                        case 10:
                                        case 12:
                                        case 13:
                                        case 34:
                                        case 92:
                                            i += w[n];
                                            break;
                                        default:
                                            if (32 > n) {
                                                i += "\\u00" + u(2, n.toString(16));
                                                break
                                            }
                                            i += s ? a[t] : l ? e.charAt(t) : e[t]
                                    }
                                }
                                return i + '"'
                            },
                            g = function(a, v, E, x, m, l, N) {
                                var i = v[a],
                                    n,
                                    o,
                                    h,
                                    d,
                                    C,
                                    T,
                                    w,
                                    p,
                                    b;
                                try {
                                    i = v[a]
                                } catch(S) {}
                                if ("object" == typeof i && i) {
                                    n = r.call(i);
                                    if ("[object Date]" == n && !s.call(i, "toJSON")) if (i > -1 / 0 && 1 / 0 > i) {
                                        if (f) {
                                            h = _(i / 864e5);
                                            for (n = _(h / 365.2425) + 1970 - 1; f(n + 1, 0) <= h; n++);
                                            for (o = _((h - f(n, 0)) / 30.42); f(n, o + 1) <= h; o++);
                                            h = 1 + h - f(n, o);
                                            d = (i % 864e5 + 864e5) % 864e5;
                                            C = _(d / 36e5) % 24;
                                            T = _(d / 6e4) % 60;
                                            w = _(d / 1e3) % 60;
                                            d %= 1e3
                                        } else {
                                            n = i.getUTCFullYear();
                                            o = i.getUTCMonth();
                                            h = i.getUTCDate();
                                            C = i.getUTCHours();
                                            T = i.getUTCMinutes();
                                            w = i.getUTCSeconds();
                                            d = i.getUTCMilliseconds()
                                        }
                                        i = (0 >= n || n >= 1e4 ? (0 > n ? "-": "+") + u(6, 0 > n ? -n: n) : u(4, n)) + "-" + u(2, o + 1) + "-" + u(2, h) + "T" + u(2, C) + ":" + u(2, T) + ":" + u(2, w) + "." + u(3, d) + "Z"
                                    } else i = t;
                                    else if ("function" == typeof i.toJSON && ("[object Number]" != n && "[object String]" != n && "[object Array]" != n || s.call(i, "toJSON"))) i = i.toJSON(a)
                                }
                                E && (i = E.call(v, a, i));
                                if (i === t) return "null";
                                n = r.call(i);
                                if ("[object Boolean]" == n) return "" + i;
                                if ("[object Number]" == n) return i > -1 / 0 && 1 / 0 > i ? "" + i: "null";
                                if ("[object String]" == n) return y("" + i);
                                if ("object" == typeof i) {
                                    for (a = N.length; a--;) if (N[a] === i) throw TypeError();
                                    N.push(i);
                                    p = [];
                                    v = l;
                                    l += m;
                                    if ("[object Array]" == n) {
                                        o = 0;
                                        for (a = i.length; a > o; b || (b = e), o++) {
                                            n = g(o, i, E, x, m, l, N);
                                            p.push(n === c ? "null": n)
                                        }
                                        a = b ? m ? "[\n" + l + p.join(",\n" + l) + "\n" + v + "]": "[" + p.join(",") + "]": "[]"
                                    } else {
                                        $(x || i,
                                            function(t) {
                                                var n = g(t, i, E, x, m, l, N);
                                                n !== c && p.push(y(t) + ":" + (m ? " ": "") + n);
                                                b || (b = e)
                                            });
                                        a = b ? m ? "{\n" + l + p.join(",\n" + l) + "\n" + v + "}": "{" + p.join(",") + "}": "{}"
                                    }
                                    N.pop();
                                    return a
                                }
                            };
                        a.stringify = function(_, e, t) {
                            var n, a, s;
                            if ("function" == typeof e || "object" == typeof e && e) if ("[object Function]" == r.call(e)) a = e;
                            else if ("[object Array]" == r.call(e)) {
                                s = {};
                                for (var o = 0,
                                         c = e.length,
                                         i; c > o; i = e[o++], ("[object String]" == r.call(i) || "[object Number]" == r.call(i)) && (s[i] = 1));
                            }
                            if (t) if ("[object Number]" == r.call(t)) {
                                if ((t -= t % 1) > 0) {
                                    n = "";
                                    for (t > 10 && (t = 10); n.length < t; n += " ");
                                }
                            } else "[object String]" == r.call(t) && (n = t.length <= 10 ? t: t.slice(0, 10));
                            return g("", (i = {},
                                i[""] = _, i), a, s, n, "", [])
                        }
                    }
                    if (!d("json-parse")) {
                        var T = String.fromCharCode,
                            C = {
                                92 : "\\",
                                34 : '"',
                                47 : "/",
                                98 : "\b",
                                116 : "	",
                                110 : "\n",
                                102 : "\f",
                                114 : "\r"
                            },
                            i,
                            p,
                            n = function() {
                                i = p = t;
                                throw SyntaxError()
                            },
                            h = function() {
                                for (var s = p,
                                         c = s.length,
                                         o, _, a, u, r; c > i;) {
                                    r = s.charCodeAt(i);
                                    switch (r) {
                                        case 9:
                                        case 10:
                                        case 13:
                                        case 32:
                                            i++;
                                            break;
                                        case 123:
                                        case 125:
                                        case 91:
                                        case 93:
                                        case 58:
                                        case 44:
                                            o = l ? s.charAt(i) : s[i];
                                            i++;
                                            return o;
                                        case 34:
                                            o = "@";
                                            for (i++; c > i;) {
                                                r = s.charCodeAt(i);
                                                if (32 > r) n();
                                                else if (92 == r) {
                                                    r = s.charCodeAt(++i);
                                                    switch (r) {
                                                        case 92:
                                                        case 34:
                                                        case 47:
                                                        case 98:
                                                        case 116:
                                                        case 110:
                                                        case 102:
                                                        case 114:
                                                            o += C[r];
                                                            i++;
                                                            break;
                                                        case 117:
                                                            _ = ++i;
                                                            for (a = i + 4; a > i; i++) {
                                                                r = s.charCodeAt(i);
                                                                r >= 48 && 57 >= r || r >= 97 && 102 >= r || r >= 65 && 70 >= r || n()
                                                            }
                                                            o += T("0x" + s.slice(_, i));
                                                            break;
                                                        default:
                                                            n()
                                                    }
                                                } else {
                                                    if (34 == r) break;
                                                    r = s.charCodeAt(i);
                                                    for (_ = i; r >= 32 && 92 != r && 34 != r;) r = s.charCodeAt(++i);
                                                    o += s.slice(_, i)
                                                }
                                            }
                                            if (34 == s.charCodeAt(i)) {
                                                i++;
                                                return o
                                            }
                                            n();
                                        default:
                                            _ = i;
                                            if (45 == r) {
                                                u = e;
                                                r = s.charCodeAt(++i)
                                            }
                                            if (r >= 48 && 57 >= r) {
                                                for (48 == r && (r = s.charCodeAt(i + 1), r >= 48 && 57 >= r) && n(); c > i && (r = s.charCodeAt(i), r >= 48 && 57 >= r); i++);
                                                if (46 == s.charCodeAt(i)) {
                                                    for (a = ++i; c > a && (r = s.charCodeAt(a), r >= 48 && 57 >= r); a++);
                                                    a == i && n();
                                                    i = a
                                                }
                                                r = s.charCodeAt(i);
                                                if (101 == r || 69 == r) {
                                                    r = s.charCodeAt(++i); (43 == r || 45 == r) && i++;
                                                    for (a = i; c > a && (r = s.charCodeAt(a), r >= 48 && 57 >= r); a++);
                                                    a == i && n();
                                                    i = a
                                                }
                                                return + s.slice(_, i)
                                            }
                                            u && n();
                                            if ("true" == s.slice(i, i + 4)) {
                                                i += 4;
                                                return e
                                            }
                                            if ("false" == s.slice(i, i + 5)) {
                                                i += 5;
                                                return ! 1
                                            }
                                            if ("null" == s.slice(i, i + 4)) {
                                                i += 4;
                                                return t
                                            }
                                            n()
                                    }
                                }
                                return "$"
                            },
                            m = function(t) {
                                var i, r;
                                "$" == t && n();
                                if ("string" == typeof t) {
                                    if ("@" == (l ? t.charAt(0) : t[0])) return t.slice(1);
                                    if ("[" == t) {
                                        for (i = [];; r || (r = e)) {
                                            t = h();
                                            if ("]" == t) break;
                                            if (r) if ("," == t) {
                                                t = h();
                                                "]" == t && n()
                                            } else n();
                                            "," == t && n();
                                            i.push(m(t))
                                        }
                                        return i
                                    }
                                    if ("{" == t) {
                                        for (i = {};; r || (r = e)) {
                                            t = h();
                                            if ("}" == t) break;
                                            if (r) if ("," == t) {
                                                t = h();
                                                "}" == t && n()
                                            } else n(); ("," == t || "string" != typeof t || "@" != (l ? t.charAt(0) : t[0]) || ":" != h()) && n();
                                            i[t.slice(1)] = m(h())
                                        }
                                        return i
                                    }
                                    n()
                                }
                                return t
                            },
                            E = function(e, i, t) {
                                t = b(e, i, t);
                                t === c ? delete e[i] : e[i] = t
                            },
                            b = function(n, s, e) {
                                var t = n[s],
                                    i;
                                if ("object" == typeof t && t) if ("[object Array]" == r.call(t)) for (i = t.length; i--;) E(t, i, e);
                                else $(t,
                                        function(i) {
                                            E(t, i, e)
                                        });
                                return e.call(n, s, t)
                            };
                        a.parse = function(o, e) {
                            var s, a;
                            i = 0;
                            p = "" + o;
                            s = m(h());
                            "$" != h() && n();
                            i = p = t;
                            return e && "[object Function]" == r.call(e) ? b((a = {},
                                a[""] = s, a), "", e) : s
                        }
                    }
                }
                v && define(function() {
                    return a
                })
            } (this);
        return JSON
    } ();
I$(100,
    function(_m, _p, _o, _f, _r) {
        if ("trident" === _m._$KERNEL.engine && "2.0" == _m._$KERNEL.release) I$(119,
            function() {
                JSON.parse = function() {
                    var _isSafeJSON = function(t) {
                        return ! /[^,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]/.test(t.replace(/"(\\.|[^"\\])*"/g, ""))
                    };
                    return JSON.parse._$aop(function(_event) {
                        var _str = _event.args[0] || "";
                        if (_str.length >= 5e5) {
                            _event.stopped = !0;
                            _event.value = eval("(" + _str + ")")
                        }
                    })
                } ()
            });
        return JSON
    },
    33);
I$(74,
    function() {
        return JSON
    },
    100);
I$(97,
    function(_, i, a, o, e, s, c, h, r, u, n, l) {
        var t;
        r._$$ProxyAbstract = _._$klass();
        t = r._$$ProxyAbstract._$extend(s._$$EventTarget);
        t.__reset = function(n) {
            this.__super(n);
            this.__request = i._$fetch({
                    url: "",
                    sync: !1,
                    cookie: !1,
                    type: "text",
                    method: "GET",
                    timeout: 6e4
                },
                n);
            var t = o._$get("csrf");
            if (t.cookie && t.param) {
                var r = encodeURIComponent(t.param) + "=" + encodeURIComponent(c._$cookie(t.cookie) || ""),
                    s = this.__request.url.indexOf("?") < 0 ? "?": "&";
                this.__request.url += s + r
            }
            this.__headers = n.headers || {};
            var a = this.__headers[e._$HEAD_CT];
            if (null == a) this.__headers[e._$HEAD_CT] = e._$HEAD_CT_FORM
        };
        t.__destroy = function() {
            this.__super();
            delete this.__rkey;
            delete this.__request;
            delete this.__headers
        };
        t.__onLoadRequest = function(i) {
            var t = i.status;
            if ( - 1 != t) if (0 == ("" + t).indexOf("2")) this._$dispatchEvent("onload", a._$text2type(i.result, this.__request.type));
            else this._$dispatchEvent("onerror", {
                    data: t,
                    result: i.result,
                    code: e._$CODE_ERRSERV,
                    message: "服务器返回异常状态[" + t + "]!"
                });
            else this._$dispatchEvent("onerror", {
                code: e._$CODE_TIMEOUT,
                message: "请求[" + this.__request.url + "]超时！"
            })
        };
        t.__doSendRequest = n;
        t.__getResponseHeader = n;
        t._$send = function(t) {
            var i = this.__request.url;
            if (i) try {
                this.__request.data = null == t ? null: t;
                var n = {
                    request: this.__request,
                    headers: this.__headers
                };
                try {
                    this._$dispatchEvent("onbeforerequest", n)
                } catch(r) {
                    console.error(r.message);
                    console.error(r.stack)
                }
                this.__doSendRequest(n)
            } catch(s) {
                this._$dispatchEvent("onerror", {
                    code: e._$CODE_ERRSERV,
                    message: "请求[" + i + "]失败:" + s.message + "！"
                })
            } else this._$dispatchEvent("onerror", {
                code: e._$CODE_NOTASGN,
                message: "没有输入请求地址！"
            })
        };
        t._$abort = n;
        t._$header = function(t) {
            if (!i._$isArray(t)) return this.__getResponseHeader(t) || "";
            var e = {};
            i._$forEach(t,
                function(t) {
                    e[t] = this._$header(t)
                },
                this);
            return e
        };
        return r
    },
    1, 4, 2, 43, 19, 5, 29, 74);
I$(118,
    function(t, e, i, n) {
        t.__getXMLHttpRequest = function() {
            return new XMLHttpRequest
        };
        return t
    });
I$(98,
    function(t, e, i, n, r, s, a) {
        if ("trident" === t._$KERNEL.engine && t._$KERNEL.release <= "2.0") I$(117,
            function() {
                e.__getXMLHttpRequest = function() {
                    var t = ["Msxml2.XMLHTTP.6.0", "Msxml2.XMLHTTP.3.0", "Msxml2.XMLHTTP.4.0", "Msxml2.XMLHTTP.5.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP"];
                    return function() {
                        var e = null;
                        i._$forIn(t,
                            function(t) {
                                try {
                                    e = new ActiveXObject(t);
                                    return ! 0
                                } catch(i) {}
                            });
                        return e
                    }
                } ()
            });
        return e
    },
    33, 118, 4);
I$(71,
    function(r, e, s, i, a, n, _, o, c) {
        var t;
        n._$$ProxyXHR = s._$klass();
        t = n._$$ProxyXHR._$extend(r._$$ProxyAbstract);
        t.__destroy = function() {
            this.__super();
            window.clearTimeout(this.__timer);
            delete this.__timer;
            try {
                this.__xhr.onreadystatechange = o;
                this.__xhr.abort()
            } catch(t) {}
            delete this.__xhr
        };
        t.__doSendRequest = function() {
            var t = function(t, e) {
                this.__xhr.setRequestHeader(e, t)
            };
            var n = function(i) {
                var t = [];
                e._$reverseEach(i.getElementsByTagName("input"),
                    function(i) {
                        if ("file" == i.type) if (i.name) {
                            if (i.files.length > 1) {
                                e._$forEach(i.files,
                                    function(e) {
                                        t.push({
                                            name: i.name,
                                            file: e
                                        })
                                    });
                                i.parentNode.removeChild(i)
                            }
                        } else i.parentNode.removeChild(i)
                    });
                return t.length > 0 ? t: null
            };
            return function(o) {
                var r = o.request,
                    s = o.headers;
                this.__xhr = a.__getXMLHttpRequest();
                if (s[i._$HEAD_CT] === i._$HEAD_CT_FILE) {
                    delete s[i._$HEAD_CT];
                    this.__xhr.upload.onprogress = this.__onStateChange._$bind(this, 1);
                    if ("FORM" === r.data.tagName) {
                        var _ = n(r.data);
                        r.data = new FormData(r.data);
                        e._$forEach(_,
                            function(t) {
                                var i = t.file;
                                r.data.append(t.name || i.name || "file-" + e._$uniqueID(), i)
                            })
                    }
                }
                this.__xhr.onreadystatechange = this.__onStateChange._$bind(this, 2);
                if (0 !== r.timeout) this.__timer = window.setTimeout(this.__onStateChange._$bind(this, 3), r.timeout);
                this.__xhr.open(r.method, r.url, !r.sync);
                e._$loop(s, t, this);
                if (this.__request.cookie && "withCredentials" in this.__xhr) this.__xhr.withCredentials = !0;
                this.__xhr.send(r.data)
            }
        } ();
        t.__onStateChange = function(t) {
            switch (t) {
                case 1:
                    this._$dispatchEvent("onuploading", arguments[1]);
                    break;
                case 2:
                    if (4 == this.__xhr.readyState) this.__onLoadRequest({
                        status: this.__xhr.status,
                        result: this.__xhr.responseText || ""
                    });
                    break;
                case 3:
                    this.__onLoadRequest({
                        status:
                            -1
                    })
            }
        };
        t.__getResponseHeader = function(t) {
            return ! this.__xhr ? "": this.__xhr.getResponseHeader(t)
        };
        t._$abort = function() {
            this.__onLoadRequest({
                status: 0
            })
        };
        return n
    },
    97, 4, 1, 19, 98);
I$(136,
    function(t, i, s, a, o) {
        var e = this,
            n = t._$KERNEL.prefix.pro,
            r = t._$is("desktop") ? 80 : t._$is("ios") ? 50 : 30;
        i.__requestAnimationFrame = function() {
            var i = t._$is("android") ? null: e.requestAnimationFrame || e[n + "RequestAnimationFrame"];
            return function() {
                if (!i) i = function(t) {
                    return window.setTimeout(function() {
                            try {
                                t( + new Date)
                            } catch(e) {}
                        },
                        1e3 / r)
                };
                return i.apply(this, arguments)
            }
        } ();
        i.__cancelAnimationFrame = function() {
            var i = t._$is("android") ? null: e.cancelAnimationFrame || e[n + "CancelAnimationFrame"];
            return function() {
                if (!i) i = function(t) {
                    window.clearTimeout(t)
                };
                return i.apply(this, arguments)
            }
        } ();
        return i
    },
    33);
I$(132,
    function(t, e) {
        return t
    },
    136, 33);
I$(123,
    function(i, e, t, n, r, s) {
        t.requestAnimationFrame = function() {
            e.__requestAnimationFrame.apply(null, arguments)
        };
        t.cancelAnimationFrame = function() {
            e.__cancelAnimationFrame.apply(null, arguments)
        };
        if (!0) {
            if (!this.requestAnimationFrame) this.requestAnimationFrame = t.requestAnimationFrame;
            if (!this.cancelAnimationFrame) this.cancelAnimationFrame = t.cancelAnimationFrame
        }
        return t
    },
    33, 132);
I$(133,
    function(e, t, i, n, r) {
        t.__canFlashEventBubble = function(t) {
            return "transparent" != (t || "").toLowerCase()
        };
        return t
    },
    33);
I$(124,
    function(t, e, i, n, r, s) {
        if ("trident" === e._$KERNEL.engine) I$(134,
            function() {
                t.__canFlashEventBubble = function(t) {
                    return ! 0
                }
            });
        if ("webkit" === e._$KERNEL.engine) I$(135,
            function() {
                t.__canFlashEventBubble = function(t) {
                    return ! 0
                }
            });
        return t
    },
    133, 33);
I$(125, '{var hide  = defined("hidden")&&!!hidden}\n{var param = defined("params")&&params||NEJ.O}\n{var width = !hide?width:"1px",height = !hide?height:"1px"}\n{if hide}<div style="position:absolute;top:0;left:0;width:1px;height:1px;z-index:10000;overflow:hidden;">{/if}\n<object classid = "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"\n        codebase = "http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab"\n        width = "${width|default:"100px"}"\n        height = "${height|default:"100px"}" id="${id}">\n    <param value="${src}" name="movie">\n    {for x in param}\n    <param value="${x}" name="${x_key}"/>\n    {/for}\n    <embed src="${src}" name="${id}"\n           width="${width|default:"100px"}"\n           height="${height|default:"100px"}"\n           pluginspage="http://www.adobe.com/go/getflashplayer"\n           type="application/x-shockwave-flash"\n           {for x in param}${x_key}="${x}" {/for}></embed>\n</object>\n{if hide}</div>{/if}');
I$(116,
    function(i, e, _, t, r, s, c, o, n, u, h, l) {
        var a = r._$add(o);
        n._$flash = function() {
            var n = {},
                o, h = /^(?:mouse.*|(?:dbl)?click)$/i;
            window.onflashevent = function(t) {
                var e = decodeURIComponent(t.target),
                    i = t.type.toLowerCase();
                var r = n[e + "-tgt"];
                if (r && h.test(i)) f(r, t);
                var s = n[e + "-on" + i];
                if (s) {
                    var a = "";
                    try {
                        a = s(t)
                    } catch(o) {}
                    return a
                }
            };
            var l = function(t) {
                o = document.title;
                var i = e._$get(t.parent) || document.body,
                    n = r._$get(a, t);
                i.insertAdjacentHTML(!t.hidden ? "beforeEnd": "afterBegin", n)
            };
            var f = function(t, e) {
                var i = e.type.toLowerCase();
                s.requestAnimationFrame(function() {
                    _._$dispatchEvent(t, i)
                })
            };
            var d = function(t) {
                return !! t && !!t.inited && !!t.inited()
            };
            var u = function(i) {
                var s = [document.embeds[i], e._$get(i), document[i], window[i]],
                    _ = t._$forIn(s, d),
                    a = s[_],
                    r = i + "-count";
                n[r]++;
                if (! (a || n[r] > 100)) window.setTimeout(u._$bind(null, i), 300);
                else {
                    if (o) {
                        document.title = o;
                        o = null
                    }
                    n[i](a);
                    delete n[i];
                    delete n[r]
                }
            };
            var p = function(i) {
                var s = i.id,
                    r = i.params;
                if (!r) {
                    r = {};
                    i.params = r
                }
                var a = r.flashvars || "";
                a += (!a ? "": "&") + ("id=" + s);
                if (!i.hidden && (i.target || c.__canFlashEventBubble(r.wmode))) {
                    var o = e._$id(i.target) || e._$id(i.parent);
                    n[s + "-tgt"] = o
                }
                r.flashvars = a;
                t._$loop(i,
                    function(e, i) {
                        if (t._$isFunction(e) && "onready" != i) n[s + "-" + i] = e
                    })
            };
            return function(e) {
                e = i.X({},
                    e);
                if (e.src) {
                    var r = "_" + t._$uniqueID();
                    e.id = r;
                    p(e);
                    l(e);
                    if (e.onready) {
                        n[r] = e.onready;
                        n[r + "-count"] = 0;
                        u(r)
                    }
                }
            }
        } ();
        if (!0) i.copy(i.P("nej.e"), n);
        return n
    },
    15, 2, 3, 4, 22, 123, 124, 125);
I$(93,
    function(_, a, s, e, o, n, h, u, c) {
        var r, t = {},
            i = e._$uniqueID();
        this["ld" + i] = function(e, n) {
            var i = t[e];
            if (i) {
                delete t[e];
                i.__onLoadRequest({
                    status: 200,
                    result: n
                })
            }
        };
        this["er" + i] = function(e, n) {
            var i = t[e];
            if (i) {
                delete t[e];
                i.__onLoadRequest({
                    status: n || 0
                })
            }
        };
        n._$$ProxyFlash = a._$klass();
        r = n._$$ProxyFlash._$extend(_._$$ProxyAbstract);
        r.__doSendRequest = function(r) {
            var a = t.flash;
            if (!e._$isArray(a)) if (a) {
                this.__rkey = e._$uniqueID();
                t[this.__rkey] = this;
                var n = e._$fetch({
                        url: "",
                        data: null,
                        method: "GET"
                    },
                    r.request);
                n.key = this.__rkey;
                n.headers = r.headers;
                n.onerror = "cb.er" + i;
                n.onloaded = "cb.ld" + i;
                var _ = s._$getFlashProxy(n.url);
                if (_) n.policyURL = _;
                a.request(n)
            } else {
                t.flash = [this.__doSendRequest._$bind(this, r)];
                o._$flash({
                    hidden: !0,
                    src: s._$get("ajax.swf"),
                    onready: function(i) {
                        if (i) {
                            var n = t.flash;
                            t.flash = i;
                            e._$reverseEach(n,
                                function(t, e, i) {
                                    try {
                                        t()
                                    } catch(n) {}
                                })
                        }
                    }
                })
            } else a.push(this.__doSendRequest._$bind(this, r))
        };
        r._$abort = function() {
            delete t[this.__rkey];
            this.__onLoadRequest({
                status: 0
            })
        };
        return n
    },
    97, 1, 43, 4, 116);
I$(76,
    function(t, e, i, n) {
        t.__formatOrigin = function() {
            var t = /^([\w]+?:\/\/.*?(?=\/|$))/i;
            return function(e) {
                e = e || "";
                if (t.test(e)) return RegExp.$1;
                else return "*"
            }
        } ();
        t.__formatPassData = function(t) {
            return t
        };
        t.__postMessage = function(n, i) {
            if (n.postMessage) {
                i = i || e;
                n.postMessage(t.__formatPassData(i.data), t.__formatOrigin(i.origin))
            }
        };
        return t
    });
I$(38,
    function(t, e, i, n, s, r, a, o) {
        if ("trident" === t._$KERNEL.engine && t._$KERNEL.release >= "4.0" && t._$KERNEL.release <= "5.0") I$(73,
            function() {
                e.__formatPassData = function(t) {
                    return JSON.stringify(t)
                }
            });
        if ("trident" === t._$KERNEL.engine && t._$KERNEL.release <= "3.0") I$(75,
            function(a) {
                var t = "MSG|",
                    s = [];
                var o = function() {
                    var s = unescape(window.name || "").trim();
                    if (s && 0 == s.indexOf(t)) {
                        window.name = "";
                        var r = i._$string2object(s.replace(t, ""), "|"),
                            a = (r.origin || "").toLowerCase();
                        if (!a || "*" == a || 0 == location.href.toLowerCase().indexOf(a)) n._$dispatchEvent(window, "message", {
                            data: JSON.parse(r.data || "null"),
                            source: window.frames[r.self] || r.self,
                            origin: e.__formatOrigin(r.ref || document.referrer)
                        })
                    }
                };
                var _ = function() {
                    var t;
                    var e = function(e, n, r) {
                        if (i._$indexOf(t, e.w) < 0) {
                            t.push(e.w);
                            r.splice(n, 1);
                            e.w.name = e.d
                        }
                    };
                    return function() {
                        t = [];
                        i._$reverseEach(s, e);
                        t = null
                    }
                } ();
                e.__postMessage = function() {
                    var e = function(e) {
                        var n = {};
                        e = e || r;
                        n.origin = e.origin || "";
                        n.ref = location.href;
                        n.self = e.source;
                        n.data = JSON.stringify(e.data);
                        return t + i._$object2string(n, "|", !0)
                    };
                    return function(t, i) {
                        s.unshift({
                            w: t,
                            d: escape(e(i))
                        })
                    }
                } ();
                a._$$CustomEvent._$allocate({
                    element: window,
                    event: "message"
                });
                setInterval(_, 100);
                setInterval(o, 20)
            },
            34, 74);
        return e
    },
    33, 76, 4, 3);
I$(10,
    function(e, i, n, r, t, s, a, o) {
        t._$postMessage = function() {
            var e = window.name || "_parent",
                t = [];
            t["_top"] = window.top;
            t["_self"] = window;
            t["_parent"] = window.parent;
            return function(a, o) {
                if (i._$isString(a)) {
                    a = t[a] || window.frames[a] || (n._$get(a) || s).contentWindow;
                    if (!a) return
                }
                var _ = i._$fetch({
                        data: null,
                        origin: "*",
                        source: e
                    },
                    o);
                r.__postMessage(a, _)
            }
        } ();
        if (!0) e.copy(e.P("nej.j"), t);
        return t
    },
    15, 4, 2, 38);
I$(94,
    function(o, e, a, r, _, s, c, n, l, u, h) {
        var i, t = {};
        n._$$ProxyFrame = a._$klass();
        i = n._$$ProxyFrame._$extend(o._$$ProxyAbstract);
        i.__init = function() {
            var e = "NEJ-AJAX-DATA:",
                i = !1;
            var n = function(r) {
                var i = r.data;
                if (0 == i.indexOf(e)) {
                    i = JSON.parse(i.replace(e, ""));
                    var n = t[i.key];
                    if (n) {
                        delete t[i.key];
                        i.result = decodeURIComponent(i.result || "");
                        n.__onLoadRequest(i)
                    }
                }
            };
            var s = function() {
                if (!i) {
                    i = !0;
                    r._$addEvent(window, "message", n)
                }
            };
            return function() {
                this.__super();
                s()
            }
        } ();
        i.__doSendRequest = function(n) {
            var u = n.request,
                i = _._$getFrameProxy(u.url),
                a = t[i];
            if (!e._$isArray(a)) if (a) {
                this.__rkey = e._$uniqueID();
                t[this.__rkey] = this;
                var o = e._$fetch({
                        url: "",
                        data: null,
                        timeout: 0,
                        method: "GET"
                    },
                    u);
                o.key = this.__rkey;
                o.headers = n.headers;
                c._$postMessage(t[i], {
                    data: o
                })
            } else {
                t[i] = [this.__doSendRequest._$bind(this, n)];
                s._$createXFrame({
                    src: i,
                    visible: !1,
                    onload: function(n) {
                        var s = t[i];
                        t[i] = r._$getElement(n).contentWindow;
                        e._$reverseEach(s,
                            function(t) {
                                try {
                                    t()
                                } catch(e) {}
                            })
                    }
                })
            } else a.push(this.__doSendRequest._$bind(this, n))
        };
        i._$abort = function() {
            delete t[this.__rkey];
            this.__onLoadRequest({
                status: 0
            })
        };
        return n
    },
    97, 4, 1, 3, 43, 2, 10);
I$(95,
    function(c, h, _, n, r, i, u, d, s, a, f, l) {
        var t, o = {},
            e = "NEJ-UPLOAD-RESULT:";
        s._$$ProxyUpload = h._$klass();
        t = s._$$ProxyUpload._$extend(c._$$ProxyAbstract);
        t.__init = function() {
            var t = !1;
            var i = function(n) {
                var t = n.data;
                if (0 == t.indexOf(e)) {
                    t = JSON.parse(t.replace(e, ""));
                    var i = o[t.key];
                    if (i) {
                        delete o[t.key];
                        i.__onLoadRequest(decodeURIComponent(t.result))
                    }
                }
            };
            var r = function() {
                if (!t) {
                    t = !0;
                    n._$addEvent(window, "message", i)
                }
            };
            return function() {
                this.__super();
                r()
            }
        } ();
        t.__destroy = function() {
            this.__super();
            r._$remove(this.__frame);
            delete this.__frame;
            window.clearTimeout(this.__timer);
            delete this.__timer
        };
        t.__onLoadRequest = function(t) {
            try {
                var e = r._$text2type(t, this.__request.type);
                this._$dispatchEvent("onload", e)
            } catch(n) {
                this._$dispatchEvent("onerror", {
                    code: i._$CODE_ERREVAL,
                    message: t
                })
            }
        };
        t.__doSendRequest = function() {
            var s = function() {
                var t, i;
                try {
                    var t = this.__frame.contentWindow.document.body,
                        i = (t.innerText || t.textContent || "").trim();
                    if (i.indexOf(e) >= 0 || t.innerHTML.indexOf(e) >= 0) return
                } catch(n) {
                    return
                }
                this.__onLoadRequest(i)
            };
            var t = function(e, i, n) {
                u._$request(e, {
                    type: "json",
                    method: "POST",
                    cookie: n,
                    mode: parseInt(i) || 0,
                    onload: function(r) {
                        if (this.__timer) {
                            this._$dispatchEvent("onuploading", r);
                            this.__timer = window.setTimeout(t._$bind(this, e, i, n), 1e3)
                        }
                    }._$bind(this),
                    onerror: function(r) {
                        if (this.__timer) this.__timer = window.setTimeout(t._$bind(this, e, i, n), 1e3)
                    }._$bind(this)
                })
            };
            return function(u) {
                var l = u.request,
                    d = u.headers,
                    e = l.data,
                    c = _._$uniqueID();
                o[c] = this;
                e.target = c;
                e.method = "POST";
                e.enctype = i._$HEAD_CT_FILE;
                e.encoding = i._$HEAD_CT_FILE;
                var h = e.action || "",
                    f = h.indexOf("?") <= 0 ? "?": "&";
                e.action = h + f + "_proxy_=form";
                this.__frame = r._$createXFrame({
                    name: c,
                    onload: function(r) {
                        var o = n._$getElement(r);
                        n._$addEvent(o, "load", s._$bind(this));
                        e.submit();
                        var i = (e.nej_query || a).value;
                        if (i) {
                            var _ = (e.nej_mode || a).value,
                                c = "true" === (e.nej_cookie || a).value;
                            this.__timer = window.setTimeout(t._$bind(this, i, _, c), 100)
                        }
                    }._$bind(this)
                })
            }
        } ();
        t._$abort = function() {
            this._$dispatchEvent("onerror", {
                code: i._$CODE_ERRABRT,
                message: "客户端终止文件上传"
            })
        };
        return s
    },
    97, 1, 4, 3, 2, 19, 36, 10);
I$(96,
    function(e, i, n, r, t, s, a, o) {
        t.__getProxyByMode = function(t, s, a) {
            var o = s ? {
                2 : r._$$ProxyUpload
            }: {
                2 : n._$$ProxyFrame,
                3 : i._$$ProxyFlash
            };
            return (o[t] || e._$$ProxyXHR)._$allocate(a)
        };
        return t
    },
    71, 93, 94, 95);
I$(72,
    function(e, t, i, n, r, s) {
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release <= "5.0") I$(99,
            function() {
                t.__getProxyByMode = function() {
                    var e = {
                        0 : 2,
                        1 : 3
                    };
                    return t.__getProxyByMode._$aop(function(n) {
                        var t = n.args,
                            i = t[0] || 0;
                        t[0] = t[1] ? 2 : e[i] || i
                    })
                } ()
            });
        return t
    },
    33, 96);
I$(36,
    function(s, r, e, u, c, o, t, _, n, h) {
        var i = {},
            a = n;
        t._$abort = function(e) {
            var t = i[e];
            if (t) t.req._$abort()
        };
        t._$filter = function(t) {
            a = t || n
        };
        t._$request = function() {
            var u = (location.protocol + "//" + location.host).toLowerCase();
            var h = function(i) {
                var t = e._$url2origin(i);
                return !! t && t != u
            };
            var l = function(t) {
                return (t || _)[r._$HEAD_CT] == r._$HEAD_CT_FILE
            };
            var f = function(t) {
                var e = l(t.headers);
                if (!h(t.url) && !e) return c._$$ProxyXHR._$allocate(t);
                else return o.__getProxyByMode(t.mode, e, t)
            };
            var d = function(t, n) {
                var e = {
                    data: n
                };
                var i = t.result.headers;
                if (i) e.headers = t.req._$header(i);
                return e
            };
            var p = function(e) {
                var t = i[e];
                if (t) {
                    if (t.req) t.req._$recycle();
                    delete i[e]
                }
            };
            var t = function(o, e) {
                var t = i[o];
                if (t) {
                    var r = arguments[2];
                    if ("onload" == e && t.result) r = d(t, r);
                    p(o);
                    var s = {
                        type: e,
                        result: r
                    };
                    a(s);
                    if (!s.stopped)(t[e] || n)(s.result)
                }
            };
            var m = function(e, i) {
                t(e, "onload", i)
            };
            var v = function(e, i) {
                t(e, "onerror", i)
            };
            var s = function(i, t) {
                var n = i.indexOf("?") < 0 ? "?": "&",
                    t = t || "";
                if (e._$isObject(t)) t = e._$object2query(t);
                if (t) i += n + t;
                return i
            };
            return function(r, t) {
                t = t || {};
                var a = e._$uniqueID(),
                    o = {
                        result: t.result,
                        onload: t.onload || n,
                        onerror: t.onerror || n
                    };
                i[a] = o;
                t.onload = m._$bind(null, a);
                t.onerror = v._$bind(null, a);
                if (t.query) r = s(r, t.query);
                var _ = t.method || "";
                if ((!_ || /get/i.test(_)) && t.data) {
                    r = s(r, t.data);
                    t.data = null
                }
                t.url = r;
                o.req = f(t);
                o.req._$send(t.data);
                return a
            }
        } ();
        t._$upload = function(i, s) {
            i = u._$get(i);
            if (!i) return "";
            var n = e._$fetch({
                    mode: 0,
                    type: "json",
                    query: null,
                    cookie: !1,
                    headers: {},
                    onload: null,
                    onerror: null,
                    onuploading: null,
                    onbeforerequest: null
                },
                s);
            n.data = i;
            n.method = "POST";
            n.timeout = 0;
            n.headers[r._$HEAD_CT] = r._$HEAD_CT_FILE;
            return t._$request(i.action, n)
        };
        if (!0) s.copy(s.P("nej.j"), t);
        return t
    },
    15, 19, 4, 2, 71, 72);
I$(68,
    function(i, n, s, r, e, a, o, _) {
        var t;
        e._$$LoaderText = n._$klass();
        t = e._$$LoaderText._$extend(i._$$LoaderAbstract);
        t.__getRequest = function() {
            return null
        };
        t.__doRequest = function() {
            r._$request(this.__url, {
                method: "GET",
                type: "text",
                onload: this.__onLoaded._$bind(this),
                onerror: this.__onError._$bind(this)
            })
        };
        t.__onLoaded = function(t) {
            this.__doCallback("onload", {
                url: this.__url,
                content: t
            })
        };
        return e
    },
    37, 1, 2, 36);
I$(114,
    function(e, t, i, n, r) {
        t.__removeIFrameKeepHistory = function(t) {
            e._$remove(t)
        };
        return t
    },
    2);
I$(92,
    function(t, i, e, n, r, s, a) {
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release <= "2.0") I$(115,
            function() {
                t.__removeIFrameKeepHistory = function(t) {
                    i._$setStyle(t, "display", "none");
                    try {
                        t.contentWindow.document.body.innerHTML = "&nbsp;"
                    } catch(e) {}
                }
            });
        return t
    },
    114, 2, 33);
I$(69,
    function(r, s, a, i, e, n, o, _) {
        var t;
        e._$$LoaderHtml = s._$klass();
        t = e._$$LoaderHtml._$extend(r._$$LoaderAbstract);
        t.__getRequest = function() {
            var t = a._$create("iframe");
            t.width = 0;
            t.height = 0;
            t.style.display = "none";
            return t
        };
        t.__doRequest = function(t) {
            try {
                document.body.appendChild(t);
                t.src = this.__url
            } catch(e) {
                console.log(t);
                console.error(e)
            }
        };
        t.__onError = function(t) {
            var e = (this.__getLoadData(this.__url) || n).request;
            this.__doCallback("onerror", t);
            i.__removeIFrameKeepHistory(e)
        };
        t.__onLoaded = function() {
            var e = null,
                t = (this.__getLoadData(this.__url) || n).request;
            try {
                if (t.src != this.__url) return;
                e = t.contentWindow.document.body
            } catch(r) {}
            this.__doCallback("onload", e);
            i.__removeIFrameKeepHistory(t)
        };
        return e
    },
    37, 1, 2, 92);
I$(70,
    function(i, n, r, t, s, a, o) {
        var e;
        t._$$LoaderStyle = n._$klass();
        e = t._$$LoaderStyle._$extend(i._$$LoaderAbstract);
        e.__getRequest = function() {
            return r._$create("link")
        };
        e.__doRequest = function(t) {
            t.href = this.__url;
            document.head.appendChild(t)
        };
        return t
    },
    37, 1, 2);
I$(9,
    function(n, r, i, e, s, a, o) {
        var t;
        e._$$LoaderScript = r._$klass();
        t = e._$$LoaderScript._$extend(n._$$LoaderAbstract);
        t.__reset = function(t) {
            this.__super(t);
            this.__async = t.async;
            this.__charset = t.charset;
            this.__qopt.async = !1;
            this.__qopt.charset = this.__charset
        };
        t.__getRequest = function() {
            var t = i._$create("script");
            if (null != this.__async) t.async = !!this.__async;
            if (null != this.__charset) t.charset = this.__charset;
            return t
        };
        t.__doClearRequest = function(t) {
            i._$remove(t)
        };
        return e
    },
    37, 1, 2);
I$(35,
    function(e, r, s, i, n, t, a, o, _) {
        t._$loadScript = function(t, e) {
            n._$$LoaderScript._$allocate(e)._$load(t)
        };
        t._$queueScript = function(t, e) {
            n._$$LoaderScript._$allocate(e)._$queue(t)
        };
        t._$loadStyle = function(t, e) {
            i._$$LoaderStyle._$allocate(e)._$load(t)
        };
        t._$queueStyle = function(t, e) {
            i._$$LoaderStyle._$allocate(e)._$queue(t)
        };
        t._$loadHtml = function(t, e) {
            s._$$LoaderHtml._$allocate(e)._$load(t)
        };
        t._$loadText = function(t, e) {
            r._$$LoaderText._$allocate(e)._$load(t)
        };
        if (!0) e.copy(e.P("nej.j"), t);
        return t
    },
    15, 68, 69, 70, 9);
I$(6,
    function(o, i, c, e, _, f, l, r, s, h, t, a, d, p) {
        var n = {},
            u = "ntp-" + +new Date + "-";
        t.tpl = function() {
            return n
        };
        t._$parseTemplate = function() {
            var n = 0;
            var o = function() {
                if (! (n > 0)) {
                    n = 0;
                    c._$dispatchEvent(document, "templateready");
                    c._$clearEvent(document, "templateready")
                }
            };
            var u = function(s, r) {
                var t = e._$dataset(s, "src");
                if (t) {
                    r = r || a;
                    var n = r.root;
                    if (!n) n = s.ownerDocument.location.href;
                    else n = i._$absolute(n);
                    t = t.split(",");
                    i._$forEach(t,
                        function(t, e, r) {
                            r[e] = i._$absolute(t, n)
                        });
                    return t
                }
            };
            var h = function(t) {
                if (_._$is("mac") && "safari" === _._$KERNEL.browser) return i._$unescape(t.innerHTML);
                else return t.value || t.innerText || ""
            };
            var d = function(t, n) {
                if (t) {
                    var i = u(t, n);
                    if (i) r._$queueStyle(i, {
                        version: e._$dataset(t, "version")
                    });
                    e._$addStyle(t.value)
                }
            };
            var p = function(t) {
                n--;
                e._$addScript(t);
                o()
            };
            var b = function(t, i) {
                if (t) {
                    var s = u(t, i),
                        a = t.value;
                    if (!s) e._$addScript(a);
                    else {
                        n++;
                        var i = {
                            version: e._$dataset(t, "version"),
                            onload: p._$bind(null, a)
                        };
                        window.setTimeout(r._$queueScript._$bind(r, s, i), 0)
                    }
                }
            };
            var v = function(e) {
                n--;
                t._$parseTemplate(e);
                o()
            };
            var g = function(t, i) {
                if (t) {
                    var s = u(t, i)[0];
                    if (s) {
                        n++;
                        var i = {
                            version: e._$dataset(t, "version"),
                            onload: v
                        };
                        window.setTimeout(r._$loadHtml._$bind(r, s, i), 0)
                    }
                }
            };
            var $ = function(e, i) {
                n--;
                t._$addTextTemplate(e, i || "");
                o()
            };
            var N = function(t, r) {
                if (t && t.id) {
                    var a = t.id,
                        i = u(t, r)[0];
                    if (i) {
                        n++;
                        var o = i + (i.indexOf("?") < 0 ? "?": "&") + (e._$dataset(t, "version") || ""),
                            r = {
                                type: "text",
                                method: "GET",
                                onload: $._$bind(null, a)
                            };
                        window.setTimeout(s._$request._$bind(s, o, r), 0)
                    }
                }
            };
            var m = function(e, i) {
                var n = e.name.toLowerCase();
                switch (n) {
                    case "jst":
                        f._$addTemplate(h(e), e.id);
                        return;
                    case "txt":
                        t._$addTextTemplate(e.id, h(e));
                        return;
                    case "ntp":
                        t._$addNodeTemplate(h(e), e.id);
                        return;
                    case "js":
                        b(e, i);
                        return;
                    case "css":
                        d(e, i);
                        return;
                    case "html":
                        g(e, i);
                        return;
                    case "res":
                        N(e, i);
                        return
                }
            };
            l._$$CustomEvent._$allocate({
                element: document,
                event: "templateready",
                oneventadd: o
            });
            return function(t, n) {
                t = e._$get(t);
                if (t) {
                    var r = "TEXTAREA" == t.tagName ? [t] : i._$object2array(t.getElementsByTagName("textarea"));
                    i._$forEach(r,
                        function(t) {
                            m(t, n)
                        });
                    e._$remove(t, !0)
                }
                o()
            }
        } ();
        t._$addTextTemplate = function(t, e) {
            if (null != n[t] && typeof n[t] == typeof e) {
                console.warn("text template overwrited with key " + t);
                console.debug("old template content: " + n[t].replace(/\n/g, " "));
                console.debug("new template content: " + e.replace(/\n/g, " "))
            }
            n[t] = e || ""
        };
        t._$getTextTemplate = function(t) {
            return n[t] || ""
        };
        t._$addNodeTemplate = function(n, r) {
            r = r || i._$uniqueID();
            n = e._$get(n) || n;
            t._$addTextTemplate(u + r, n);
            if (!i._$isString(n)) e._$removeByEC(n);
            return r
        };
        t._$getNodeTemplate = function(r) {
            if (!r) return null;
            r = u + r;
            var n = t._$getTextTemplate(r);
            if (!n) return null;
            var s;
            if (i._$isString(n)) {
                n = e._$html2node(n);
                var a = n.getElementsByTagName("textarea");
                if (! ("TEXTAREA" == n.tagName || a && a.length)) t._$addTextTemplate(r, n);
                else s = n
            }
            if (!s) s = n.cloneNode(!0);
            e._$removeByEC(s);
            return s
        };
        t._$getItemTemplate = function() {
            var t = function(e, t) {
                return "offset" == t || "limit" == t
            };
            return function(r, l, s) {
                var c = [];
                if (!r || !r.length || !l) return c;
                s = s || a;
                var f = r.length,
                    u = parseInt(s.offset) || 0,
                    h = Math.min(f, u + (parseInt(s.limit) || f)),
                    _ = {
                        total: r.length,
                        range: [u, h]
                    };
                i._$merge(_, s, t);
                for (var o = u,
                         e; h > o; o++) {
                    _.index = o;
                    _.data = r[o];
                    e = l._$allocate(_);
                    var d = e._$getId();
                    n[d] = e;
                    e._$recycle = e._$recycle._$aop(function(t, e) {
                        delete n[t];
                        delete e._$recycle
                    }._$bind(null, d, e));
                    c.push(e)
                }
                return c
            }
        } ();
        t._$getItemById = function(t) {
            return n[t]
        };
        t._$parseUITemplate = function() {
            var n = /#<(.+?)>/g;
            return function(s, r) {
                r = r || {};
                s = (s || "").replace(n,
                    function(n, e) {
                        var t = r[e];
                        if (!t) {
                            t = "tpl-" + i._$uniqueID();
                            r[e] = t
                        }
                        return t
                    });
                t._$parseTemplate(e._$html2node(s));
                return r
            }
        } ();
        h._$merge({
            _$parseTemplate: t._$parseTemplate,
            _$addNodeTemplate: t._$addNodeTemplate
        });
        if (!0) o.copy(o.P("nej.e"), t);
        return t
    },
    15, 4, 3, 2, 33, 22, 34, 35, 36, 17);
I$(25,
    function(e, i, n, r) {
        var t = {
            404 : "网络异常，请刷新页面重试",
            "-1": "网络不好，请刷新页面重试",
            "-2": "网络不好，请刷新页面重试",
            0 : "网络不好，请刷新页面重试",
            401 : "操作超时，请刷新页面重试",
            CHECK_USER_EMPTY: "请输入帐号",
            CHECK_USER_BAD: "请输入正确的邮箱或手机号码",
            CHECK_USER_TOO_LONG: "您输入的帐号太长了",
            CHECK_URS_EMPTY: "请输入帐号",
            CHECK_URS_BAD_BEGIN: "帐号须由字母开头",
            CHECK_URS_BAD_MB: "帐号须由字母开头",
            CHECK_URS_BAD_END: "帐号请以字母或数字结尾",
            CHECK_URS_BAD_LENGTH: "帐号须由6-18个字符组成",
            CHECK_URS_BAD_ILLEGAL: "请用字母、数字或下划线命名",
            CHECK_PASSWORD_EMPTY: "请设置您的登录密码",
            CHECK_PASSWORD_LENGTH: "密码须由6-16个字符组成，区分大小写",
            CHECK_PASSWORD_SIMPLE: "密码过于简单，请重新设置",
            CHECK_PASSWORD_EQUAL: "密码与注册帐号不能相同",
            CHECK_PASSWORD_CHARCODE255: "密码不符合规范",
            CHECK_PASSWORD_HASEMPTY: "密码不能包含空格",
            CHECK_PASSWORD2_EMPTY: "请重复输入密码",
            CHECK_PASSWORD2_DIFF: "密码不一致",
            CHECK_SMS_EMPTY: "请输入正确的验证码",
            CHECK_SMS_BAD: "请输入正确的验证码",
            CHECK_CAPTCHA_EMPTY: "请输入正确的验证码",
            CHECK_CAPTCHA_BAD: "请输入正确的验证码",
            CHECK_PERSON_ID_EMPTY: "请输入您的身份证号码",
            CHECK_PERSON_NAME_EMPTY: "请输入您的姓名",
            CHECK_MOBILE_EMPTY: "请输入正确的手机号",
            CHECK_MOBILE_BAD: "请输入正确的手机号",
            EXCEPTION_INIT_COMPONENT_401: "初始化失败，参数不齐全",
            EXCEPTION_INIT_COMPONENT_433: "初始化失败，参数不匹配",
            EXCEPTION_INIT_COMPONENT_500: "服务器异常，我们正努力抢修中",
            EXCEPTION_CHECK_NAME_106: "抱歉，该帐号不允许注册",
            EXCEPTION_CHECK_NAME_401: "操作超时，请刷新页面重试",
            EXCEPTION_CHECK_NAME_407: "该帐号已注册",
            EXCEPTION_CHECK_NAME_409: "注册尝试次数太频繁",
            EXCEPTION_CHECK_NAME_410: "超过IP限制，请稍候再试",
            EXCEPTION_CHECK_NAME_422: "该帐号已注册",
            EXCEPTION_CHECK_NAME_500: "服务器异常，我们正努力抢修中",
            EXCEPTION_CHECK_NAME_504: "次数超限，请稍候再试",
            EXCEPTION_CHECK_NAME_505: "次数超限，请稍候再试",
            EXCEPTION_GET_TICKET_106: "抱歉，该帐号不允许注册",
            EXCEPTION_GET_TICKET_108: "请输入正确的验证码",
            EXCEPTION_GET_TICKET_109: "请滑动滑块验证码",
            EXCEPTION_GET_TICKET_401: "操作超时，请刷新页面重试",
            EXCEPTION_GET_TICKET_407: "该帐号已注册",
            EXCEPTION_GET_TICKET_409: "注册尝试次数太频繁",
            EXCEPTION_GET_TICKET_410: "超过IP限制，请稍候再试",
            EXCEPTION_GET_TICKET_500: "服务器异常，我们正努力抢修中",
            EXCEPTION_FAST_REG_107: "请输入正确的验证码",
            EXCEPTION_FAST_REG_106: "抱歉，该帐号不允许注册",
            EXCEPTION_FAST_REG_401: "操作超时，请刷新页面重试",
            EXCEPTION_FAST_REG_402: "当前网络异常，请检查您的网络环境",
            EXCEPTION_FAST_REG_407: "帐号已存在",
            EXCEPTION_FAST_REG_409: "注册尝试次数太频繁",
            EXCEPTION_FAST_REG_410: "超过IP限制，请稍候再试",
            EXCEPTION_FAST_REG_412: "验证码错误次数过多，请改天再试",
            EXCEPTION_FAST_REG_413: "请输入正确的验证码",
            EXCEPTION_FAST_REG_500: "服务器异常，我们正努力抢修中",
            EXCEPTION_REG_MOB_401: "操作超时，请刷新页面重试",
            EXCEPTION_REG_MOB_402: "当前网络异常，请检查您的网络环境",
            EXCEPTION_REG_MOB_407: "帐号已存在",
            EXCEPTION_REG_MOB_410: "超过IP限制，请稍候再试",
            EXCEPTION_REG_MOB_412: "错误次数太多",
            EXCEPTION_REG_MOB_413: "请输入正确的验证码",
            EXCEPTION_REG_MOB_421: "该手机号已被注册",
            EXCEPTION_REG_MOB_423: "该手机号是保留帐号",
            EXCEPTION_REG_MOB_500: "服务器异常，我们正努力抢修中",
            EXCEPTION_GET_SMS_107: "请输入正确的验证码",
            EXCEPTION_GET_SMS_108: "请输入正确的验证码",
            EXCEPTION_GET_SMS_109: "请滑动滑块验证码",
            EXCEPTION_GET_SMS_401: "操作超时，请刷新页面重试",
            EXCEPTION_GET_SMS_410: "超过IP限制，请稍候再试",
            EXCEPTION_GET_SMS_411: "请您编辑短信”0000000000”发送到”000000”，完成注册",
            EXCEPTION_GET_SMS_412: "下发验证码超过了次数限制，请改天再试",
            EXCEPTION_GET_SMS_413: "请输入正确的验证码",
            EXCEPTION_GET_SMS_421: "该手机号已被注册",
            EXCEPTION_GET_SMS_423: "该手机号是保留帐号",
            EXCEPTION_GET_SMS_500: "服务器异常，我们正努力抢修中",
            EXCEPTION_SEND_MAIL_104: "帐号不存在。",
            EXCEPTION_SEND_MAIL_106: "用户不是外部邮箱。",
            EXCEPTION_SEND_MAIL_401: "操作超时，请刷新页面重试。",
            EXCEPTION_SEND_MAIL_410: "超过IP限制，请稍候再试。",
            EXCEPTION_SEND_MAIL_421: "帐号已被注册。",
            EXCEPTION_SEND_MAIL_500: "服务器异常，我们正努力抢修中。",
            EXCEPTION_SEND_MAIL_503: "服务器维护中，请稍后再试。",
            EXCEPTION_SEND_MAIL_504: "重发邮件次数超过限制。",
            MODAL_MAIL_SUCCESS_TITLE: "激活邮件已再次发送！",
            MODAL_MAIL_SUCCESS_TEXT: "请查看您的邮箱。",
            MODAL_MAIL_SUCCESS_BUTTON: "立即查看邮件",
            MODAL_MAIL_ERROR_TITLE: "出错了"
        };
        return t
    });
I$(11,
    function(m, t, c, h, o, s, _, a, e, g, v, p) {
        var u = {
            mobile: /^(13|14|15|17|18)\d{9}$/,
            netease: /^[a-zA-Z]([a-zA-Z]|\d|_){4,16}([a-zA-Z]|\d)$/
        };
        var n, f = {
                "qq.com": "1",
                "sina.com": "1",
                "sogou.com": "1",
                "gmail.com": "1",
                "foxmail.com": "1",
                "sohu.com": "1",
                "vip.qq.com": "1",
                "yahoo.com.tw": "1",
                "2980.com": "1",
                "hotmail.com": "1",
                "live.com": "1",
                "139.com": "1",
                "tom.com": "1",
                "outlook.com": "1",
                "mail.ru": "1",
                "icloud.com": "1",
                "aliyun.com": "1",
                "mail.edu.tw": "1",
                "hotmail.es": "1",
                "3344.at": "1",
                "21cn.com": "1"
            },
            d = {
                qq: "1",
                renren: "2",
                weibo: "3",
                weixin: "13",
                yixin: "8"
            };
        var r = function() {
            var e = t._$get("cnt-box-parent").clientWidth || document.body.scrollWidth,
                i = document.body.clientHeight,
                n = {
                    width: e,
                    height: i,
                    type: "resize"
                };
            if (e * i > 0) {
                n["URS-CM"] = 1;
                _._$postMessage("_parent", {
                    data: n
                })
            }
        };
        e._$resize = r;
        var l = function(n, _, i, s) {
            s = s || "";
            var c = t._$get("cnt-box-parent");
            var a = n && e._$getParent(n, "inputbox");
            if (a) t._$addClassName(a, "error-color");
            var i = t._$get(i);
            i.innerHTML = o._$get("error-tmp", {
                str: _ || "",
                type: s
            });
            i.className = "m-nerror";
            if (n) {
                t._$dataset(i, "from", n.name);
                t._$addClassName(i, "err_" + n.name)
            } else if (0 == n) t._$dataset(i, "from", "0");
            else t._$dataset(i, "from", "null");
            t._$addClassName(c, "haserr");
            r()
        };
        e._$isBadNetease = function(t) {
            return ! u.netease.test(t)
        };
        e._$isNeteaseEmail = function(t) {
            return "163.com" === t || "126.com" === t || "yeah.net" === t || "vip.163.com" == t || "vip.126.com" == t || "188.com" == t
        };
        e._$checkMobile = function(t) {
            return u.mobile.test(t)
        };
        e._$getParent = function(e, i) {
            e = t._$get(e);
            e = e.parentElement || e.parentNode;
            for (; e != document.body;) {
                if (!e) return null;
                if (t._$hasClassName(e, i)) break;
                else e = e.parentElement || e.parentNode
            }
            return e
        };
        e._$showError = function(t, e, i, n) {
            l(t, e, i, n)
        };
        e._$showError2 = function(n, a, i, _) {
            var u = t._$get("cnt-box-parent");
            var s = n && e._$getParent(n, "inputbox");
            if (s) t._$addClassName(s, "error-color");
            if (0 == _) {
                var i = t._$get(i);
                i.innerHTML = o._$get("error-tmp", {
                    str: a || ""
                });
                i.className = "m-nerror";
                if (n) {
                    t._$dataset(i, "from", n.name);
                    t._$addClassName(i, "err_" + n.name)
                } else t._$dataset(i, "from", "null")
            }
            c._$addEvent(i, "click",
                function() {
                    if (n) n.focus();
                    else {
                        t._$addClassName(i, "f-dn");
                        r()
                    }
                });
            t._$addClassName(u, "haserr");
            r()
        };
        e._$removeError = function(s, i) {
            var _ = t._$get("cnt-box-parent");
            var a = t._$dataset(i, "from");
            var n = s.name;
            var o = s && e._$getParent(s, "inputbox");
            if (o) t._$delClassName(o, "error-color");
            if (a == n || "null" == a) {
                var i = t._$get(i);
                t._$addClassName(i, "f-dn");
                if ("email" == n) t._$delClassName(i, "err_email");
                else if ("password" == n) t._$delClassName(i, "err_password");
                else if ("checkcode" == n) t._$delClassName(i, "err_checkcode");
                else if ("phone" == n) t._$delClassName(i, "err_phone");
                else if ("phonecode" == n) t._$delClassName(i, "err_phonecode")
            }
            if (0 != a) t._$delClassName(_, "haserr");
            r()
        };
        e._$removeError2 = function() {
            t._$addClassName(t._$get("nerror"), "f-dn");
            r()
        };
        e._$showFail = function(e) {
            if ("601" != e) {
                n = clearTimeout(n);
                t._$remove("failbox", !0);
                var a = parseInt(s._$KERNEL.version, 10);
                var o = "trident" == s._$KERNEL.engine && 10 > a ? "boxtop": "";
                var i = 500 == e ? "系统繁忙，请您稍后再试。": "检测出非法请求，为了您的帐号安全，请您稍后再试。",
                    _ = 500 == e ? "fail0 ": "fail1 ",
                    r = t._$create("div", _ + o, t._$getByClassName(document, "g-bd")[0]);
                if ("-1" == e || "-2" == e) i = "网络不好，请刷新页面重试";
                r.id = "failbox";
                r.innerHTML = '<div class="box">' + i + "</div>";
                n = setTimeout(function() {
                        t._$remove("failbox", !0)
                    },
                    5e3)
            }
        };
        e._$showFail2 = function(i) {
            n = clearTimeout(n);
            t._$remove("failbox", !0);
            var r = parseInt(s._$KERNEL.version, 10);
            var a = "trident" == s._$KERNEL.engine && 10 > r ? "boxtop": "";
            var o = "fail1 ",
                e = t._$create("div", o + a, t._$getByClassName(document, "g-bd")[0]);
            e.id = "failbox";
            e.innerHTML = '<div class="box">' + i + "</div>";
            n = setTimeout(function() {
                    t._$remove("failbox", !0)
                },
                5e3)
        };
        e._$hideFail = function() {
            t._$remove("failbox", !0)
        };
        e._$supportCss3 = function(i) {
            var n = ["webkit", "Moz", "ms", "o"],
                t,
                e = [],
                s = document.documentElement.style,
                r = function(t) {
                    return t.replace(/-(\w)/g,
                        function(e, t) {
                            return t.toUpperCase()
                        })
                };
            for (t in n) e.push(r(n[t] + "-" + i));
            e.push(r(i));
            for (t in e) if (e[t] in s) return ! 0;
            return ! 1
        };
        e._$getCommonEmail = function(t) {
            var e = t.split("@")[1];
            return f[e] ? "http://mail." + t.substr(t.indexOf("@") + 1) : ""
        };
        e._$loadGaq = function() {
            _gaq = window["_gaq"] || []
        };
        e._$timeCount = function(t) {
            try {
                NRUM.mark(t + "_s")
            } catch(e) {}
            if (!window.timecount) window.timecount = [];
            window.timecount[t] = (new Date).getTime()
        };
        e._$timeCountEnd = function(t) {
            try {
                NRUM.mark(t + "_end");
                NRUM.measure(t, t + "_s", t + "_end")
            } catch(i) {}
            if (!window.timecount) return 0;
            if (!window.timecount[t]) return 0;
            var e = (new Date).getTime() - window.timecount[t];
            window.timecount[t] = 0;
            return e
        };
        e._$requestJsonp = function(s, n, a, o) {
            var _ = (new Date).getTime();
            var e = "jsonp" + _;
            window["qrcb"] = [];
            window["qrcb"][e] = a;
            var i = "";
            for (var r in n) i += "&" + r + "=" + n[r];
            i = i.slice(1);
            var c = s + "?" + i + "&callback=qrcb." + e;
            var t = document.getElementById("mp-script-" + e);
            if (!t) {
                t = document.createElement("script");
                t.type = "text/javascript";
                t.id = "mp-script-" + e;
                t.src = c;
                document.getElementsByTagName("head")[0].appendChild(t)
            }
            if (!o) document.getElementsByTagName("head")[0].appendChild(t)
        };
        e._$postMessage = function(t, e) {
            _._$postMessage(t, e)
        };
        e._$showSuccLoading = function() {
            var e = t._$get("loading");
            if (e) t._$delClassName(e, "f-dn")
        };
        e._$parseOauth = function() {
            var t = window.URSCONFIG.oauthLoginConfig || !1;
            if (!t) return t;
            var e = location.protocol + "//reg.163.com/outerLogin/oauth2/connect.do?product=" + window.URSCONFIG.product;
            h._$forEach(t,
                function(t) {
                    if (!t.url) if ("alipay" == t.name) {
                        e = e.replace("/outerLogin/oauth2/connect.do", "/outerLogin/oauth2/aliPayFastLogin.do");
                        t.url = e
                    } else t.url = e + "&target=" + d[t.name];
                    var i = {
                        url: location.protocol + "//webzj.reg.163.com/webapp/res/statichtml/third.html",
                        url2: location.protocol + "//webzj.reg.163.com/webapp/res/statichtml/third.html"
                    };
                    if (t.backurl) {
                        i = {
                            url: location.protocol + "//webzj.reg.163.com/webapp/res/statichtml/third.html?backurl=" + t.backurl,
                            url2: location.protocol + "//webzj.reg.163.com/webapp/res/statichtml/third.html?backurl=" + t.backurl
                        };
                        if ("alipay" == t.name) i = {
                            redirect_error: location.protocol + "//webzj.reg.163.com/webapp/res/statichtml/third.html?backurl=" + t.backurl,
                            redirect_url: location.protocol + "//webzj.reg.163.com/webapp/res/statichtml/third.html?backurl=" + t.backurl
                        }
                    } else if ("alipay" == t.name) i = {
                        redirect_error: location.protocol + "//webzj.reg.163.com/webapp/res/statichtml/third.html",
                        redirect_url: location.protocol + "//webzj.reg.163.com/webapp/res/statichtml/third.html"
                    };
                    t.url += "&" + h._$object2query(i)
                });
            return t
        };
        e._$doThirdLogin = function(s) {
            var e = c._$getElement(s),
                i = t._$dataset(e, "link"),
                a = t._$dataset(e, "width"),
                o = t._$dataset(e, "height");
            if (i) {
                var n = a || 514;
                var r = o || 764;
                var _ = (window.screen.availHeight - 30 - n) / 2;
                var u = (window.screen.availWidth - 10 - r) / 2;
                window.open(i, "thirdLogin", "height=" + n + ",width=" + r + ",top=" + _ + ",left=" + u + ",toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no")
            }
        };
        e._$checkPwd = function() {
            var t = function(t) {
                var n = t.charAt(0),
                    i = !0;
                for (var e = 1,
                         r = t.length; r > e; e++) if (n !== t.charAt(e)) {
                    i = !1;
                    break
                }
                return i
            };
            var e = function(n) {
                var e = ["123456", "123456789", "12345678", "123123", "5201314", "1234567", "7758521", "654321", "1314520", "123321", "1234567890", "147258369", "123654", "5211314", "woaini", "1230123", "987654321", "147258", "123123123", "7758258", "520520", "789456", "456789", "159357", "112233", "1314521", "456123", "110110", "521521", "zxcvbnm", "789456123", "0123456789", "0123456", "123465", "159753", "qwertyuiop", "987654", "115415", "1234560", "123000", "123789", "100200", "963852741", "121212", "111222", "123654789", "12301230", "456456", "741852963", "asdasd", "asdfghjkl", "369258", "863786", "258369", "8718693", "666888", "5845201314", "741852", "168168", "iloveyou", "852963", "4655321", "102030", "147852369", "321321"];
                var i = !1;
                for (var t = 0,
                         r = e.length; r > t; t++) if (e[t] === n) {
                    i = !0;
                    break
                }
                return i
            };
            var n = function(e, t) {
                var i = t.substr(0, t.indexOf("@")) || t;
                return e === i || e === t
            };
            var r = function(t) {
                for (i = 0; i < t.length; i++) if (t.charCodeAt(i) > 255) return 1
            };
            return function(i, _) {
                i = i.trim() || "";
                var o = i.length,
                    s;
                if (6 > o || o > 16) s = a.CHECK_PASSWORD_LENGTH;
                else if (t(i) || e(i) || /^\d{1,9}$/.test(i)) s = a.CHECK_PASSWORD_SIMPLE;
                else if (n(i, _)) s = a.CHECK_PASSWORD_EQUAL;
                else if (r(i)) s = a.CHECK_PASSWORD_CHARCODE255;
                return s
            }
        } ();
        e.__sendClose = function() {
            var t = {
                type: "close"
            };
            t["URS-CM"] = 1;
            e._$postMessage("_parent", {
                data: t
            })
        };
        e._$regSuccess = function(s, n, a) {
            var _ = t._$get("cnt-box");
            _parent2 = t._$get("cnt-box2");
            o._$render(_parent2, "register-success", {
                username: n,
                mobile: a
            });
            t._$setStyle(_, "display", "none");
            t._$setStyle(_parent2, "display", "block");
            var i = s || 3;
            t._$get("countdown").innerHTML = i + "秒后自动登录";
            var r = setInterval(function() {
                i -= 1;
                if (0 != i) t._$get("countdown").innerHTML = i + "秒后自动登录";
                else {
                    r = clearInterval(r);
                    var s = {
                        type: "register-success",
                        username: n,
                        "URS-CM": 1
                    };
                    e._$postMessage("_parent", {
                        data: s
                    });
                    e.__sendClose()
                }
            }._$bind(this), 1e3);
            e._$resize()
        };
        e._$getErrorTxt = function(t) {
            if (!t) return "服务器内部错误，请您稍后再试";
            t = t.toString();
            if ("433" == t) return "系统繁忙，请刷新页面重试";
            if (0 == t.indexOf("5")) return "服务器内部错误，请您稍后再试";
            else if (0 == t.indexOf("4")) return "请求错误，请您稍后再试";
            else return "请求错误，请您稍后再试"
        };
        e._$setOutLogin = function() {
            var e = window.$loginOpts.promark + +new Date;
            var a = window.$loginOpts.domains || "";
            var i = window.$loginOpts.cookieDomain || "";
            var n = window.$loginOpts.prdomain || "";
            var r = window.$loginOpts.needMobileLogin || "";
            var s = window.$loginOpts.mobileFirst || "";
            var t = window.$loginOpts.noqr || "";
            var o = window.$loginOpts.smsLoginFirst || "";
            var _ = window.$loginOpts.toolName || "";
            var c = window.$loginOpts.toolUrl || "";
            var u = window.$loginOpts.needQrLogin || "";
            var h = "https://webzj.reg.163.com/safelogin.html?loginKey=" + e + "&domains=" + a + "&prdomain=" + n + "&cookieDomain=" + i + "&needMobileLogin=" + r + "&mobileFirst=" + s + "&noqr=" + t + "&smsLoginFirst=" + o + "&toolName=" + _ + "&toolUrl=" + c + "&needQrLogin=" + u;
            var f = '<strong class="msg"><span style="color:#000;">您的帐号存在风险，</span><a style="color:red;font-size:14px;text-decoration:underline;font-weight:bolder;" target="_blank" href=' + h + ">点此进行安全登录</a></strong>";
            l(0, f, "nerror")
        };
        return e
    },
    1, 2, 3, 4, 22, 33, 10, 25);
I$(21,
    function(h, c, e, l, u, i, n, f, d, _) {
        var r = "dl.reg.163.com",
            s = "zc.reg.163.com",
            a = "passport.yeah.net/dl",
            o = "passport.126.com/dl";
        var t = {
            "/l": {
                name: "/l",
                201 : {
                    ret: "201",
                    message: "登录成功"
                },
                401 : {
                    ret: "401",
                    message: "参数错误"
                },
                402 : {
                    ret: "402",
                    message: "指纹错误"
                },
                423 : {
                    ret: "423",
                    message: "风控帐号"
                }
            },
            "/lpwd": {
                name: "/lpwd",
                201 : {
                    ret: "201",
                    message: "登录成功"
                }
            },
            "/lvfsms": {
                name: "/lvfsms",
                201 : {
                    ret: "201",
                    message: "登录成功"
                }
            }
        };
        var p = function(i, t) {
            if (e._$isString(t)) t = JSON.parse(t);
            if (201 != _code || 201 == _code && t.warn) i.onwarn(t);
            else i.onload(t)
        };
        var m = function(i, t) {
            if (e._$isString(t)) t = JSON.parse(t);
            i.onerror(t)
        };
        var v = function(i, t) {
            if (e._$isString(t)) t = JSON.parse(t);
            i.onbeforerequest(t)
        };
        var g = function() {
            var t = document.body.scrollWidth,
                e = document.body.scrollHeight,
                n = {
                    width: t,
                    height: e,
                    type: "resize"
                };
            if (t * e > 0) {
                n["URS-CM"] = 1;
                i._$postMessage("_parent", {
                    data: n
                })
            }
        };
        n._$request = function() {
            var e = function(e, r) {
                if (t[e]) {
                    var n = {
                        data: {}
                    };
                    var s = r.ret || -1;
                    n.data["URS-CM"] = 1;
                    n.data["URS-CM-STATE"] = t[e][s] || {
                            ret: -1
                        };
                    if (r.unprotectedGuide) t[e][s].unprotectedGuide = 1;
                    n.data["URS-CM-STATENAME"] = t[e].name;
                    i._$postMessage("_parent", n)
                }
            };
            return function(c, h, l, f, d, u) {
                var t = r;
                var n = s;
                var _;
                if ("mail126" === u) t = o;
                else if ("mailyeah" === u) t = a;
                if (window["$cookieDomain"]) if (window["$cookieDomain"].indexOf("icourse163.org") >= 0) t = "reg." + window["$cookieDomain"] + "/dl";
                else t = "passport." + window["$cookieDomain"] + "/dl";
                if (window["$regCookieDomain"]) if (window["$regCookieDomain"].indexOf("icourse163.org") >= 0) n = "reg." + window["$regCookieDomain"] + "/zc";
                else n = "passport." + window["$regCookieDomain"] + "/zc";
                if (c.indexOf("mb-") > -1) {
                    t += "/yd";
                    n += "/yd"
                }
                if (d) _ = t;
                else _ = n;
                MP[c](h,
                    function(i, t) {
                        e(i, t);
                        l(t)
                    },
                    function(n, t) {
                        e(n, t);
                        if ("601" != t.ret) f(t);
                        else i._$setOutLogin()
                    },
                    _)
            }
        } ();
        return n
    },
    1, 2, 4, 36, 10, 11);
I$(61,
    function(r, s, e, o, a, _, i, c, n, u) {
        var t;
        i._$$Abstract = s._$klass();
        t = i._$$Abstract._$extend(a._$$EventTarget);
        t.__init = function() {
            this.__super();
            e._$dumpCSSText();
            this.__initXGui();
            this.__initNode()
        };
        t.__reset = function(t) {
            this.__super(t);
            this.__doInitClass(t.clazz);
            this._$appendTo(t.parent)
        };
        t.__destroy = function() {
            this.__super();
            this.__doDelParentClass();
            delete this.__parent;
            e._$removeByEC(this.__body);
            e._$delClassName(this.__body, this.__class);
            delete this.__class
        };
        t.__initXGui = n;
        t.__initNode = function() {
            if (!this.__seed_html) this.__initNodeTemplate();
            this.__body = _._$getNodeTemplate(this.__seed_html);
            if (!this.__body) this.__body = e._$create("div", this.__seed_css);
            e._$addClassName(this.__body, this.__seed_css)
        };
        t.__initNodeTemplate = n;
        t.__doInitClass = function(t) {
            this.__class = t || "";
            e._$addClassName(this.__body, this.__class)
        };
        t.__doAddParentClass = function() {
            if (this.__seed_css) {
                var t = this.__seed_css.split(/\s+/);
                e._$addClassName(this.__parent, t.pop() + "-parent")
            }
        };
        t.__doDelParentClass = function() {
            if (this.__seed_css) {
                var t = this.__seed_css.split(/\s+/);
                e._$delClassName(this.__parent, t.pop() + "-parent")
            }
        };
        t._$getBody = function() {
            return this.__body
        };
        t._$appendTo = function(t) {
            if (this.__body) {
                this.__doDelParentClass();
                if (o._$isFunction(t)) this.__parent = t(this.__body);
                else {
                    this.__parent = e._$get(t);
                    if (this.__parent) this.__parent.appendChild(this.__body)
                }
                this.__doAddParentClass()
            }
        };
        t._$show = function() {
            if (this.__parent && this.__body && this.__body.parentNode != this.__parent) this.__parent.appendChild(this.__body)
        };
        t._$hide = function() {
            e._$removeByEC(this.__body)
        };
        if (!0) r.copy(r.P("nej.ui"), i);
        return i
    },
    15, 1, 2, 4, 5, 6);
I$(60,
    function(o, e, s, n, a, i, r, u, _, c) {
        var t, h;
        r._$$Module = o._$klass();
        t = r._$$Module._$extend(a._$$Abstract);
        t.__init = function(t) {
            this.__super(t)
        };
        t.__reset = function(t) {
            this.__super(t);
            this.__initForm();
            this.__initEvent();
            this.__states = {};
            if (!t.errMsg) this._$clearState()
        };
        t.__destroy = function() {
            this.__super();
            n._$forEach(this.__ipts,
                function(t) {
                    t = t._$recycle()
                })
        };
        t.__initNode = function() {
            this.__super()
        };
        t.__initCallback = function() {};
        t.__initErrorHandler = function() {};
        t.__setPlaceHolder = function() {
            if (this.__placeholder && !this.__placeholder2) {
                if (this.__placeholder.account) {
                    var t = e._$getByClassName(this.__body, "u-input")[0];
                    e._$getByClassName(t, "u-label")[0].innerHTML = this.__placeholder.account;
                    var n = e._$getByClassName(t, "j-inputtext")[0];
                    e._$dataset(n, "placeholder", this.__placeholder.account)
                }
                if (this.__placeholder.pwd) {
                    var i = e._$getByClassName(this.__body, "u-input")[1];
                    e._$getByClassName(i, "u-label")[0].innerHTML = this.__placeholder.pwd;
                    var r = e._$getByClassName(i, "j-inputtext")[0];
                    e._$dataset(r, "placeholder", this.__placeholder.pwd)
                }
                this.__placeholder2 = 1
            }
        };
        t._$stateOK = function(t) {
            this.__form._$checkValidity(null, 1);
            setTimeout(function() {
                var i = 1,
                    e = "";
                if (void 0 != typeof this.__states["checkcode"]) this.__states["tcheckcode"] = this.__states["checkcode"];
                n._$forIn(this.__states,
                    function(n, t) {
                        if ("checkcode" != t) if (n && !e) {
                            i = 0;
                            e = t
                        }
                    });
                t(i, e)
            }._$bind(this), 200)
        };
        t._$getValues = function() {
            var t = [];
            n._$forEach(this.__inputs,
                function(e) {
                    var i = e.value;
                    t.push(i)
                });
            return t
        };
        t._$showTip = function(t) {};
        t._$clearState = function() {
            if (this._$hideCheckCode);
            n._$reverseEach(this.__ipts,
                function(t, e) {
                    var i = e ? 0 : 1;
                    t._$onClear(i)
                }._$bind(this));
            this.__initError()
        };
        t.__initError = function() {
            var n = e._$get("nerror");
            n.innerHTML = "";
            var i = e._$getByClassName(document, "error-color");
            for (var t = 0; t < i.length; t++) e._$delClassName(i[t], "error-color")
        };
        t.__setSlideSuc = function() {
            var t = e._$getByClassName(document, "statusTxt")[0];
            if (t) t.innerHTML = '<div class="u-success u-suc"></div>';
            this.__states["slidecap"] = 0
        };
        t.__cbVftcp = function(t) {
            this.__slideCapLock = 0;
            this.__setSlideSuc();
            this.__checkNextBtn();
            if (t) this._$dispatchEvent("onSlideOk")
        };
        t.__cbVftcpEx = function(e) {
            var t;
            this.__slideCapLock = 0;
            if (e) {
                t = e.ret;
                if ("441" == t) {
                    this.__needSlideCap = 0;
                    this.__needCheckCode = 1;
                    this._$refreshCheckCode()
                } else if ("444" == t) {
                    this.__needSlideCap = 1;
                    this.__needCheckCode = 0;
                    this._$refreshCheckCode()
                } else if ("401" == t) {
                    i._$showError(null, "操作超时，请刷新页面重试", "nerror");
                    this._$getSlideCap()
                } else {
                    i._$showError(null, "请滑动验证码", "nerror");
                    this._$getSlideCap()
                }
            } else i._$showError(null, "请滑动验证码", "nerror")
        };
        t.__slidebarover = function() {
            if (this.__sdot) this.__sdot = clearTimeout(this.__sdot);
            this.__sdov = setTimeout(function() {
                this.__slideCapBox.style.zIndex = "501"
            }._$bind(this), 100)
        };
        t.__slidebarout = function() {
            if (this.__sdov) this.__sdov = clearTimeout(this.__sdov);
            this.__sdot = setTimeout(function() {
                this.__slideCapBox.style.zIndex = "19"
            }._$bind(this), 100)
        };
        t.__clearSlideErr = function() {
            i._$removeError2()
        };
        t.__vSlide = function() {
            var t = e._$get("pwd") || "";
            if (t) t = t.value;
            if ("" == t || "LG42Dm53vsrZmrXdZ6buHUVNfQcsLzql1gV7HFhl5yZzlILOJmPEY+r+vJComfirFG2deb709GYQQIob6ke6c31j6W+FKrE6QEghCshv5Kc=" == t) return 1;
            else return 0
        };
        t._$getSlideCap = function() {
            e._$get("pwd").value = "";
            if (this.__initSlideCap) {
                this.__slideOpt.forceRegenerate = !1;
                window["scaptcha"](e._$get("ScapTcha"), this.__slideOpt)
            } else {
                this.__slideOpt.forceRegenerate = !0;
                window["scaptcha"](e._$get("ScapTcha"), this.__slideOpt);
                this.__initSlideCap = 1
            }
            setTimeout(function() {
                var t = e._$getByClassName(document, "slideFg")[0];
                s._$addEvent(t, "mousedown", this.__clearSlideErr._$bind(this));
                s._$addEvent(this.__slideCapBox, "mouseout", this.__slidebarout._$bind(this));
                s._$addEvent(this.__slideCapBox, "mouseover", this.__slidebarover._$bind(this))
            }._$bind(this), 300)
        };
        t._$refreshCheckCode = function() {
            if (this.__needSlideCap) this._$showSlideCode();
            else if (this.__needCheckCode) this._$showCheckCode()
        };
        t._$hasCheckCode = function() {
            return this.__needCheckCode || this.__needSlideCap
        };
        t._$showSlideCode = function() {
            this._$hideCheckCode();
            this.__needSlideCap = 1;
            this.__states["slidecap"] = 1;
            e._$delClassName(this.__slideCapBox, "f-dn");
            this.__slideLock = 0;
            this._$getSlideCap();
            this._$dispatchEvent("ondisabled", 1);
            this.__checkNextBtn();
            setTimeout(function() {
                    i._$resize()
                },
                200)
        };
        t._$hideCheckCode = function() {
            var t = e._$get("cnt-box-parent");
            e._$delClassName(t, "hascheckcode");
            this.__states["checkcode"] = 0;
            this.__states["slidecap"] = 0;
            this.__needSlideCap = 0;
            this.__needCheckCode = 0;
            e._$addClassName(this.__checkCode, "f-dn");
            e._$addClassName(this.__slideCapBox, "f-dn");
            i._$resize()
        };
        return r
    },
    1, 2, 3, 4, 61, 11);
I$(129,
    function(t, e, i, n, r, s) {
        i.__focusElement = function() {
            var i = function(n, r) {
                var i = t._$getElement(r);
                if (!i.value) e._$delClassName(i, n)
            };
            var n = function(i, n) {
                e._$addClassName(t._$getElement(n), i)
            };
            return function(r, e, s) {
                if (1 == e) t._$addEvent(r, "blur", i._$bind(null, s));
                if (1 == e || -1 == e) t._$addEvent(r, "focus", n._$bind(null, s))
            }
        } ();
        return i
    },
    3, 2);
I$(120,
    function(e, t, i, n, r, s, a, o) {
        if ("trident" === e._$KERNEL.engine && e._$KERNEL.release <= "3.0") I$(128,
            function() {
                t.__focusElement = function() {
                    var e = function(t, e) {
                        n._$delClassName(i._$getElement(e), t)
                    };
                    return t.__focusElement._$aop(function(n) {
                        var t = n.args;
                        if (1 != t[1]) {
                            i._$addEvent(t[0], "blur", e._$bind(null, t[2]));
                            t[1] = -1
                        }
                    })
                } ()
            });
        return t
    },
    33, 129, 3, 2);
I$(101,
    function(n, e, i, r, s, t, a, o, _) {
        t._$focus = function(n, t) {
            n = i._$get(n);
            if (n) {
                var s = 0,
                    a = "js-focus";
                if (e._$isNumber(t)) s = t;
                else if (e._$isString(t)) a = t;
                else if (e._$isObject(t)) {
                    s = t.mode || s;
                    a = t.clazz || a
                }
                var o = parseInt(i._$dataset(n, "mode"));
                if (!isNaN(o)) s = o;
                o = i._$dataset(n, "focus");
                if (o) a = o;
                r.__focusElement(n, s, a)
            }
        };
        s._$merge(t);
        if (!0) n.copy(n.P("nej.e"), t);
        return t
    },
    15, 4, 2, 120, 17);
I$(127,
    function(t) {
        t.__length = function() {
            var t = /(\r\n|\r|\n)/g;
            return function(e) {
                return (e || "").replace(t, "**").length
            }
        } ();
        return t
    },
    33);
I$(121,
    function(e, t) {
        if ("trident" === e._$KERNEL.engine) I$(126,
            function() {
                t.__length = function() {
                    return (_event.args[0] || "").length
                }
            });
        return t
    },
    33, 127);
I$(102,
    function(i, t, r, n, s, a, e, _, o, c) {
        e._$counter = function() {
            var _ = /[\r\n]/gi,
                e = {};
            var s = function(t) {
                return a.__length(t)
            };
            var i = function(r) {
                var n = e[r],
                    s = t._$get(r),
                    a = t._$get(n.xid);
                if (s && n) {
                    var i = {
                        input: s.value
                    };
                    i.length = n.onlength(i.input);
                    i.delta = n.max - i.length;
                    n.onchange(i);
                    a.innerHTML = i.value || "剩余" + Math.max(0, i.delta) + "个字"
                }
            };
            return function(h, l) {
                var _ = t._$id(h);
                if (_ && !e[_]) {
                    var a = n._$merge({},
                        l);
                    a.onchange = a.onchange || o;
                    a.onlength = s;
                    if (!a.max) {
                        var c = parseInt(t._$attr(_, "maxlength")),
                            u = parseInt(t._$dataset(_, "maxLength"));
                        a.max = c || u || 100;
                        if (!c && u) a.onlength = n._$length
                    }
                    e[_] = a;
                    r._$addEvent(_, "input", i._$bind(null, _));
                    var f = t._$wrapInline(_, {
                        nid: a.nid || "js-counter",
                        clazz: a.clazz
                    });
                    a.xid = t._$id(f);
                    i(_)
                }
            }
        } ();
        s._$merge(e);
        if (!0) i.copy(i.P("nej.e"), e);
        return e
    },
    15, 2, 3, 4, 17, 121);
I$(130,
    function(t, e, i, n) {
        t.__setPlaceholder = function(t, e) {};
        return t
    });
I$(122,
    function(i, t, e, r, n, s, a, o, _) {
        if ("trident" === i._$KERNEL.engine && i._$KERNEL.release <= "5.0") I$(131,
            function() {
                n.__setPlaceholder = function() {
                    var i = {},
                        n = {
                            nid: "j-holder-" + r._$uniqueID()
                        };
                    var s = function(e) {
                        var r = t._$get(e);
                        i[e] = 2;
                        if (!r.value) t._$setStyle(t._$wrapInline(r, n), "display", "none")
                    };
                    var a = function(e) {
                        var r = t._$get(e);
                        i[e] = 1;
                        if (!r.value) t._$setStyle(t._$wrapInline(r, n), "display", "")
                    };
                    var o = function(e) {
                        var r = t._$get(e);
                        if (2 != i[e]) t._$setStyle(t._$wrapInline(r, n), "display", !r.value ? "": "none")
                    };
                    var _ = function(e, s) {
                        var a = t._$id(e),
                            i = t._$wrapInline(e, {
                                tag: "label",
                                clazz: s,
                                nid: n.nid
                            });
                        i.htmlFor = a;
                        var r = t._$attr(e, "placeholder") || "";
                        i.innerText = "null" == r ? "": r;
                        var o = e.offsetHeight + "px";
                        t._$style(i, {
                            left: 0,
                            display: !e.value ? "": "none"
                        })
                    };
                    return function(t, r) {
                        if (null == i[t.id]) {
                            _(t, r);
                            var n = t.id;
                            i[n] = 1;
                            e._$addEvent(t, "blur", a._$bind(null, n));
                            e._$addEvent(t, "focus", s._$bind(null, n));
                            e._$addEvent(t, "input", o._$bind(null, n))
                        }
                    }
                } ()
            });
        return n
    },
    33, 2, 3, 4, 130);
I$(103,
    function(e, i, n, r, t, s, a, o) {
        t._$placeholder = function(t, e) {
            t = i._$get(t);
            r.__setPlaceholder(t, i._$dataset(t, "holder") || e || "js-placeholder")
        };
        n._$merge(t);
        if (!0) e.copy(e.P("nej.e"), t);
        return t
    },
    15, 2, 17, 122);
I$(77,
    function(r, c, i, s, e, o, _, h, u, n, a, l, f, t) {
        n._$$WebForm = c._$klass();
        t = n._$$WebForm._$extend(o._$$EventTarget);
        t.__init = function() {
            this.__super();
            this.__wopt = {
                tp: {
                    nid: "js-nej-tp"
                },
                ok: {
                    nid: "js-nej-ok"
                },
                er: {
                    nid: "js-nej-er"
                }
            }
        };
        t.__reset = function(t) {
            this.__super(t);
            this.__form = document.forms[t.form] || i._$get(t.form);
            this.__doInitDomEvent([[this.__form, "enter", this._$dispatchEvent._$bind(this, "onenter")]]);
            this.__message = t.message || {};
            this.__message.pass = this.__message.pass || "&nbsp;";
            var e = this.__dataset(this.__form, "focusMode", 1);
            if (!isNaN(e)) this.__fopt = {
                mode: e,
                clazz: t.focus
            };
            this.__holder = t.holder;
            this.__wopt.tp.clazz = "js-mhd " + (t.tip || "js-tip");
            this.__wopt.ok.clazz = "js-mhd " + (t.pass || "js-pass");
            this.__wopt.er.clazz = "js-mhd " + (t.error || "js-error");
            this.__invalid = t.invalid || "js-invalid";
            this.__doInitValidRule(t);
            this._$refresh();
            if (this.__fnode) this.__fnode.focus()
        };
        t.__destroy = function() {
            this.__super();
            this._$reset();
            delete this.__message;
            delete this.__fnode;
            delete this.__vinfo;
            delete this.__xattr;
            delete this.__form;
            delete this.__treg;
            delete this.__vfun
        };
        t.__dataset = function(e, n, r) {
            var t = i._$dataset(e, n);
            switch (r) {
                case 1:
                    return parseInt(t, 10);
                case 2:
                    return "true" == (t || "").toLowerCase();
                case 3:
                    return this.__doParseDate(t)
            }
            return t
        };
        t.__number = function(t, e, i) {
            if ("date" == e) return this.__doParseDate(t, i);
            else return parseInt(t, 10)
        };
        t.__isValidElement = function() {
            var t = /^button|submit|reset|image|hidden|file$/i;
            return function(e) {
                e = this._$get(e) || e;
                var i = e.type;
                return !! e.name && !t.test(e.type || "")
            }
        } ();
        t.__isValueElement = function() {
            var t = /^hidden$/i;
            return function(e) {
                if (this.__isValidElement(e)) return ! 0;
                e = this._$get(e) || e;
                var i = e.type || "";
                return t.test(i)
            }
        } ();
        t.__doParseDate = function() {
            var t = /[:\.]/;
            return function(n, s) {
                if ("now" == (n || "").toLowerCase()) return + new Date;
                var r = e._$var2date(n);
                if (r && !t.test(n)) {
                    var i = (s || "").split(t);
                    r.setHours(parseInt(i[0], 10) || 0, parseInt(i[1], 10) || 0, parseInt(i[2], 10) || 0, parseInt(i[3], 10) || 0)
                }
                return + r
            }
        } ();
        t.__doCheckString = function(t, e) {
            var i = this.__vfun[e],
                n = this.__dataset(t, e);
            if (n && i) {
                this.__doPushValidRule(t, i);
                this.__doSaveValidInfo(t, e, n)
            }
        };
        t.__doCheckPattern = function(t, e) {
            try {
                var i = this.__dataset(t, e);
                if (!i) return;
                var n = new RegExp(i);
                this.__doSaveValidInfo(t, e, n);
                this.__doPushValidRule(t, this.__vfun[e])
            } catch(r) {}
        };
        t.__doCheckBoolean = function(t, e) {
            var i = this.__vfun[e];
            if (i && this.__dataset(t, e, 2)) this.__doPushValidRule(t, i)
        };
        t.__doCheckNumber = function(e, i, t) {
            t = parseInt(t, 10);
            if (!isNaN(t)) {
                this.__doSaveValidInfo(e, i, t);
                this.__doPushValidRule(e, this.__vfun[i])
            }
        };
        t.__doCheckDSNumber = function(t, e) {
            this.__doCheckNumber(t, e, this.__dataset(t, e))
        };
        t.__doCheckATNumber = function(t, e) {
            this.__doCheckNumber(t, e, i._$attr(t, e))
        };
        t.__doCheckTPNumber = function(t, e, n) {
            var i = this.__number(this.__dataset(t, e), this.__dataset(t, "type"));
            this.__doCheckNumber(t, e, i)
        };
        t.__doCheckCustomAttr = function(t) {
            e._$loop(this.__xattr,
                function(r, e) {
                    var n = i._$dataset(t, e);
                    if (null != n) {
                        this.__doSaveValidInfo(t, e, n);
                        this.__doPushValidRule(t, this.__vfun[e])
                    }
                },
                this)
        };
        t.__doPrepareElement = function() {
            var t = /^input|textarea$/i,
                r = /[:\.]/;
            var e = function(t) {
                this._$showTip(s._$getElement(t))
            };
            var n = function(e) {
                var t = s._$getElement(e);
                if (!this.__dataset(t, "ignore", 2)) this.__doCheckValidity(t)
            };
            return function(r) {
                if (this.__dataset(r, "autoFocus", 2)) this.__fnode = r;
                var o = i._$attr(r, "placeholder");
                if (o && "null" != o) u._$placeholder(r, this.__holder);
                if (this.__fopt && t.test(r.tagName)) _._$focus(r, this.__fopt);
                var s = i._$id(r);
                this.__doCheckBoolean(s, "required");
                this.__doCheckString(s, "type");
                this.__doCheckPattern(s, "pattern");
                this.__doCheckATNumber(s, "maxlength");
                this.__doCheckATNumber(s, "minlength");
                this.__doCheckDSNumber(s, "maxLength");
                this.__doCheckDSNumber(s, "minLength");
                this.__doCheckTPNumber(s, "min");
                this.__doCheckTPNumber(s, "max");
                this.__doCheckCustomAttr(s);
                var c = i._$dataset(s, "time");
                if (c) this.__doSaveValidInfo(s, "time", c);
                var l = r.name;
                this.__message[l + "-tip"] = this.__dataset(r, "tip");
                this.__message[l + "-error"] = this.__dataset(r, "message");
                this._$showTip(r);
                var f = this.__vinfo[s],
                    d = (f || a).data || a,
                    p = this.__dataset(r, "counter", 2);
                if (p && (d.maxlength || d.maxLength)) h._$counter(s, {
                    nid: this.__wopt.tp.nid,
                    clazz: "js-counter"
                });
                if (f && t.test(r.tagName)) this.__doInitDomEvent([[r, "focus", e._$bind(this)], [r, "blur", n._$bind(this)]]);
                else if (this.__dataset(r, "focus", 2)) this.__doInitDomEvent([[r, "focus", e._$bind(this)]])
            }
        } ();
        t.__doInitValidRule = function() {
            var t = {
                number: /^[\d]+$/i,
                url: /^[a-z]+:\/\/(?:[\w-]+\.)+[a-z]{2,6}.*$/i,
                email: /^[\w-\.]+@(?:[\w-]+\.)+[a-z]{2,6}$/i,
                email1: /^[\w-\.]+@(?:[\w-]+\.)+[a-z]{2,6}$/i,
                email2: /^[\w-\.]+$/i,
                date: function(t, i) {
                    var n = this.__dataset(i, "format") || "yyyy-MM-dd";
                    return ! t || !isNaN(this.__doParseDate(t)) && e._$format(this.__doParseDate(t), n) == t
                }
            };
            var n = {
                required: function(t) {
                    var e = t.type,
                        i = !t.value,
                        n = ("checkbox" == e || "radio" == e) && !t.checked;
                    if (n || i) return - 1;
                    else;
                },
                type: function(i, r) {
                    var t = this.__treg[r.type],
                        n = i.value.trim(),
                        s = !!t.test && !t.test(n),
                        a = e._$isFunction(t) && !t.call(this, n, i);
                    if (s || a) return - 2;
                    else;
                },
                pattern: function(t, e) {
                    if (!e.pattern.test(t.value)) return - 3;
                    else;
                },
                maxlength: function(t, e) {
                    if (t.value.length > e.maxlength) return - 4;
                    else;
                },
                minlength: function(t, e) {
                    if (t.value.length < e.minlength) return - 5;
                    else;
                },
                maxLength: function(t, i) {
                    if (e._$length(t.value) > i.maxLength) return - 4;
                    else;
                },
                minLength: function(t, i) {
                    if (e._$length(t.value) < i.minLength) return - 5;
                    else;
                },
                min: function(i, t) {
                    var e = this.__number(i.value, t.type, t.time);
                    if (isNaN(e) || e < t.min) return - 6;
                    else;
                },
                max: function(i, t) {
                    var e = this.__number(i.value, t.type, t.time);
                    if (isNaN(e) || e > t.max) return - 7;
                    else;
                }
            };
            var i = function(t, i, n, s) {
                var r = t[n];
                if (!e._$isFunction(i) || !e._$isFunction(r)) t[n] = i;
                else t[n] = r._$aop(i)
            };
            return function(s) {
                if (s.domain) t.email = t.email2;
                else t.email = t.email1;
                this.__treg = r.X({},
                    t);
                e._$loop(s.type, i._$bind(null, this.__treg));
                this.__vfun = r.X({},
                    n);
                this.__xattr = s.attr;
                e._$loop(this.__xattr, i._$bind(null, this.__vfun))
            }
        } ();
        t.__doPushValidRule = function(i, n) {
            if (e._$isFunction(n)) {
                var t = this.__vinfo[i];
                if (!t || !t.func) {
                    t = t || {};
                    t.func = [];
                    this.__vinfo[i] = t
                }
                t.func.push(n)
            }
        };
        t.__doSaveValidInfo = function(e, i, n) {
            if (i) {
                var t = this.__vinfo[e];
                if (!t || !t.data) {
                    t = t || {};
                    t.data = {};
                    this.__vinfo[e] = t
                }
                t.data[i] = n
            }
        };
        t.__doCheckValidity = function(t) {
            t = this._$get(t) || t;
            if (!t) return ! 0;
            var s = this.__vinfo[i._$id(t)];
            if (!s && this.__isValidElement(t)) {
                this.__doPrepareElement(t);
                s = this.__vinfo[i._$id(t)]
            }
            if (!s) return ! 0;
            var n;
            e._$forIn(s.func,
                function(e) {
                    n = e.call(this, t, s.data);
                    return null != n
                },
                this);
            if (null == n) {
                var r = {
                    target: t,
                    form: this.__form
                };
                this._$dispatchEvent("oncheck", r);
                n = r.value
            }
            var r = {
                target: t,
                form: this.__form
            };
            if (null != n) {
                if (e._$isObject(n)) e._$merge(r, n);
                else r.code = n;
                this._$dispatchEvent("oninvalid", r);
                if (!r.stopped) this._$showMsgError(t, r.value || this.__message[t.name + n])
            } else {
                this._$dispatchEvent("onvalid", r);
                if (!r.stopped) this._$showMsgPass(t, r.value)
            }
            return null == n
        };
        t.__doShowMessage = function() {
            var n = {
                tp: "tip",
                ok: "pass",
                er: "error"
            };
            var r = function(t, e) {
                return t == e ? "block": "none"
            };
            var s = function(n, r, s) {
                var e = t.call(this, n, r);
                if (!e && s) e = i._$wrapInline(n, this.__wopt[r]);
                return e
            };
            var t = function(e, r) {
                var t = i._$get(e.name + "-" + n[r]);
                if (!t) t = i._$getByClassName(e.parentNode, this.__wopt[r].nid)[0];
                return t
            };
            return function(n, a, o) {
                n = this._$get(n) || n;
                if (n) {
                    "er" == o ? i._$addClassName(n, this.__invalid) : i._$delClassName(n, this.__invalid);
                    var _ = s.call(this, n, o, a);
                    if (_ && a) _.innerHTML = a;
                    e._$loop(this.__wopt,
                        function(s, e) {
                            i._$setStyle(t.call(this, n, e), "display", r(o, e))
                        },
                        this)
                }
            }
        } ();
        t._$showTip = function(t, e) {
            this.__doShowMessage(t, e || this.__message[t.name + "-tip"], "tp")
        };
        t._$showMsgPass = function(t, e) {
            this.__doShowMessage(t, e || this.__message[t.name + "-pass"] || this.__message.pass, "ok")
        };
        t._$showMsgError = function(t, e) {
            this.__doShowMessage(t, e || this.__message[t.name + "-error"], "er")
        };
        t._$setValue = function() {
            var n = /^(?:radio|checkbox)$/i;
            var t = function(t) {
                return null == t ? "": t
            };
            var r = function(i, n) {
                if (n.multiple) {
                    var r;
                    if (!e._$isArray(i)) r[i] = i;
                    else r = e._$array2object(i);
                    e._$forEach(n.options,
                        function(t) {
                            t.selected = null != r[t.value]
                        })
                } else n.value = t(i)
            };
            var i = function(i, e) {
                if (n.test(e.type || "")) e.checked = i == e.value;
                else if ("SELECT" == e.tagName) r(i, e);
                else e.value = t(i)
            };
            return function(r, n) {
                var t = this._$get(r);
                if (t) if ("SELECT" == t.tagName || !t.length) i(n, t);
                else e._$forEach(t, i._$bind(null, n))
            }
        } ();
        t._$get = function(t) {
            return this.__form.elements[t]
        };
        t._$form = function() {
            return this.__form
        };
        t._$data = function() {
            var t = /^radio|checkbox$/i,
                n = /^number|date$/;
            var r = function(t) {
                if ("SELECT" == t.tagName && t.multiple) {
                    var i = [];
                    e._$forEach(t.options,
                        function(t) {
                            if (t.selected) i.push(t.value)
                        });
                    return i.length > 0 ? i: ""
                }
                return t.value
            };
            var s = function(_, a) {
                var c = a.name,
                    s = r(a),
                    o = _[c],
                    u = this.__dataset(a, "type"),
                    h = i._$dataset(a, "time");
                if (n.test(u)) if (e._$isArray(s)) e._$forEach(s,
                    function(t, e, i) {
                        i[e] = this.__number(t, u, h)
                    },
                    this);
                else s = this.__number(s, u, h);
                if (t.test(a.type) && !a.checked) {
                    s = this.__dataset(a, "value");
                    if (!s) return
                }
                if (o) {
                    if (!e._$isArray(o)) {
                        o = [o];
                        _[c] = o
                    }
                    o.push(s)
                } else _[c] = s
            };
            return function() {
                var t = {};
                e._$forEach(this.__form.elements,
                    function(e) {
                        if (this.__isValueElement(e)) s.call(this, t, e)
                    },
                    this);
                return t
            }
        } ();
        t._$reset = function() {
            var t = function(t) {
                if (this.__isValidElement(t)) this._$showTip(t)
            };
            return function() {
                this.__form.reset();
                e._$forEach(this.__form.elements, t, this)
            }
        } ();
        t._$submit = function() {
            this.__form.submit()
        };
        t._$refresh = function() {
            var t = function(t) {
                if (this.__isValidElement(t)) this.__doPrepareElement(t)
            };
            return function() {
                this.__vinfo = {};
                e._$forEach(this.__form.elements, t, this)
            }
        } ();
        t._$checkValidity = function(t, n) {
            t = this._$get(t) || t;
            if (t) return this.__doCheckValidity(t);
            var i = !0;
            e._$forEach(this.__form.elements,
                function(t) {
                    var e = this._$checkValidity(t);
                    i = i && e;
                    if (!i && n) return ! 0;
                    else;
                },
                this);
            return i
        };
        if (!0) r.copy(r.P("nej.ut"), n);
        return n
    },
    15, 1, 2, 3, 4, 5, 101, 102, 103);
I$(57,
    function(r, t, i, s, e) {
        var n;
        e._$$WebForm = r._$klass();
        n = e._$$WebForm._$extend(s._$$WebForm);
        n.__doCheckValidity = function(e) {
            e = this._$get(e) || e;
            if (!e) return ! 0;
            var s = this.__vinfo[i._$id(e)];
            if (!s && this.__isValidElement(e)) {
                this.__doPrepareElement(e);
                s = this.__vinfo[i._$id(e)]
            }
            if (!s) return ! 0;
            var r;
            t._$forIn(s.func,
                function(t) {
                    r = t.call(this, e, s.data);
                    return null != r
                },
                this);
            if (null == r) {
                var n = {
                    target: e,
                    form: this.__form
                };
                this._$dispatchEvent("oncheck", n);
                r = n.value
            }
            var n = {
                target: e,
                form: this.__form
            };
            if (null != r) {
                if (t._$isObject(r)) t._$merge(n, r);
                else n.code = r;
                this._$dispatchEvent("oninvalid", n);
                if (!n.stopped) this._$showMsgError(e, n.value || this.__message[e.name + r])
            } else {
                this._$dispatchEvent("onvalid", n);
                if (!n.stopped) this._$showMsgPass(e, n.value)
            }
            if (n.ignore) return ! 0;
            else return null == r
        };
        return e
    },
    1, 4, 2, 77);
I$(107,
    function(r, s, e, i, a, n, o, _, c, t) {
        n._$$SelectHelper = s._$klass();
        t = n._$$SelectHelper._$extend(a._$$EventTarget);
        t.__reset = function(t) {
            this.__super(t);
            this.__loop = !!t.loopable;
            this.__parent = e._$get(t.parent);
            this.__selected = t.selected || "js-selected";
            this.__hovered = t.hover || this.__selected;
            this.__nopt = {};
            if (t.clazz) {
                this.__nopt.filter = e._$hasClassName._$bind2(e, t.clazz);
                this.__clazz = t.clazz
            }
            this.__kbody = this.__getKeyBoardParent(this.__parent);
            this.__doInitDomEvent([[this.__kbody, "keydown", this.__doCheckKBAction._$bind(this), !0], [this.__kbody, "enter", this.__doCheckKBEnter._$bind(this)], [this.__parent, "click", this.__onCheckClick._$bind(this)], [this.__parent, "mouseover", this.__onCheckHover._$bind(this)], [this.__parent, "mouseleave", this.__onCheckLeave._$bind(this)]])
        };
        t.__destroy = function() {
            this.__super();
            delete this.__selected;
            delete this.__hovered;
            delete this.__parent;
            delete this.__kbody;
            delete this.__clazz;
            delete this.__nopt;
            delete this.__loop
        };
        t.__isItemElement = function(t) {
            if (this.__clazz) return e._$hasClassName(t, this.__clazz);
            else return t.parentNode == this.__parent
        };
        t.__getKeyBoardParent = function() {
            var t = 1e3;
            return function(e) {
                for (; e && (parseInt(e.tabIndex) || 0) <= t;) e = e.parentNode;
                return e || document
            }
        } ();
        t.__getItemElement = function(i) {
            var t = e._$getByClassName(this.__parent, i);
            return ! t ? null: t[0]
        };
        t.__doSyncSelection = function(t, i) {
            e._$delClassName(t.last, i);
            e._$addClassName(t.target, i);
            if (i == this.__selected && t.last != t.target) {
                this.__doScrollToView(t.target);
                this._$dispatchEvent("onchange", t)
            }
        };
        t.__doScrollToView = function(i) {
            var t = e._$getScrollViewPort(i),
                n = e._$offset(i, t);
            if (! (n.y - t.scrollTop < 0)) {
                var r = n.y + i.offsetHeight - t.clientHeight;
                if (r > t.scrollTop) t.scrollTop = r
            } else t.scrollTop = n.y
        };
        t.__doParseSelection = function(e, n) {
            var t = i._$getElement(e, this.__isItemElement._$bind(this));
            return ! t ? null: {
                last: this.__getItemElement(n),
                target: t
            }
        };
        t.__doCheckKBAction = function(s) {
            var r = s.keyCode;
            if (38 == r || 40 == r) {
                i._$stop(s);
                var t = {
                    last: this._$getSelectedNode()
                };
                this.__nopt.backward = 38 == r;
                var n = !this.__clazz ? e._$getChildren(this.__parent) : e._$getByClassName(this.__parent, this.__clazz),
                    a = this.__nopt.backward ? n[n.length - 1] : n[0];
                if (!t.last) t.target = this.__getItemElement(this.__hovered) || a;
                else t.target = e._$getSibling(t.last, this.__nopt);
                if (!t.target) {
                    if (!this.__loop || n.length <= 1) return;
                    t.target = a
                }
                this.__doSyncSelection(t, this.__selected)
            }
        };
        t.__doCheckKBEnter = function(t) {
            i._$stop(t);
            this._$dispatchEvent("onselect", {
                enter: !0,
                target: this._$getSelectedNode()
            })
        };
        t.__onCheckClick = function(e) {
            i._$stop(e);
            var t = this.__doParseSelection(e, this.__selected);
            if (t) {
                this.__doSyncSelection(t, this.__selected);
                this._$dispatchEvent("onselect", {
                    target: t.target
                })
            }
        };
        t.__onCheckHover = function(e) {
            var t = this.__doParseSelection(e, this.__hovered);
            if (t) {
                this.__doSyncSelection(t, this.__hovered);
                if (this.__kbody.focus) this.__kbody.focus()
            }
        };
        t.__onCheckLeave = function(t) {
            if (this.__hovered != this.__selected) e._$delClassName(this.__getItemElement(this.__hovered), this.__hovered)
        };
        t._$getSelectedNode = function() {
            return this.__getItemElement(this.__selected)
        };
        if (!0) r.copy(r.P("nej.ut"), n);
        return n
    },
    15, 1, 2, 3, 5);
I$(79,
    function(n, r, e, s, a, i, o, _, c) {
        var t;
        i._$$Suggest = r._$klass();
        t = i._$$Suggest._$extend(s._$$EventTarget);
        t.__init = function() {
            this.__sopt = {
                loopable: !0,
                onselect: this.__onSelect._$bind(this),
                onchange: this.__onSelectionChange._$bind(this)
            };
            this.__super()
        };
        t.__reset = function(t) {
            this.__super(t);
            this.__auto = !!t.autofill;
            this.__input = e._$get(t.input);
            this.__sopt.clazz = t.clazz;
            this.__sopt.parent = e._$get(t.body);
            this.__sopt.selected = t.selected || "js-selected";
            this.__doInitDomEvent([[this.__input, "input", this.__onInput._$bind(this)], [this.__input, "focus", this.__onInput._$bind(this)]]);
            if (!t.noblur) this.__doInitDomEvent([[this.__input, "blur", this.__onBlur._$bind(this)]]);
            this._$visibile(!1);
            this.__helper = a._$$SelectHelper._$allocate(this.__sopt)
        };
        t.__destroy = function() {
            this.__super();
            if (this.__helper) {
                this.__helper._$recycle();
                delete this.__helper
            }
            delete this.__xxx;
            delete this.__input;
            delete this.__sopt.parent
        };
        t.__onBlur = function() {
            this.__onSelect({
                target: this.__helper._$getSelectedNode()
            })
        };
        t.__onInput = function() {
            var t = this.__input.value.trim();
            if (!t) this._$visibile(!1);
            else if (!this.__xxx) this._$dispatchEvent("onchange", t)
        };
        t.__doUpdateValue = function(t) {
            if (!this.__xxx) {
                this.__xxx = !0;
                if (t && t != this.__input.value) this.__input.value = t;
                this.__xxx = !1
            }
        };
        t.__onSelect = function(i) {
            if ("hidden" != e._$getStyle(this.__sopt.parent, "visibility")) {
                var t = e._$dataset(i.target, "value") || "";
                this.__doUpdateValue(t);
                t = t || this.__input.value;
                this._$update("");
                this._$dispatchEvent("onselect", t, {
                    target: i.target,
                    enter: i.enter,
                    value: t
                })
            }
        };
        t.__onSelectionChange = function(t) {
            if (this.__auto) this.__doUpdateValue(e._$dataset(t.target, "value") || "")
        };
        t._$setList = function(t) {
            this._$visibile( !! t && t.length > 0)
        };
        t._$visibile = function(t) {
            var t = !t ? "hidden": "visible";
            this.__sopt.parent.style.visibility = t;
            if ("hidden" === t) this.__sopt.parent.innerHTML = ""
        };
        t._$update = function(t) {
            this.__sopt.parent.innerHTML = t || "&nbsp;";
            this._$visibile( !! t)
        };
        if (!0) n.copy(n.P("nej.ut"), i);
        return i
    },
    15, 1, 2, 5, 107);
I$(80, ".#<uispace>-parent{position:relative;}\n.#<uispace>{position:absolute;border:1px solid #aaa;background:#fff;text-align:left;visibility:hidden;}\n.#<uispace> .zitm{height:20px;line-height:20px;cursor:default;}\n.#<uispace> .js-selected{background:#1257F9;}");
I$(81, '{if defined("xlist")&&!!xlist.length}\n  {list xlist as x}<div class="zitm" data-value="${x}">${x}</div>{/list}\n{/if}');
I$(63,
    function(n, _, e, c, u, s, r, o, a, i, p, d, f) {
        var l = e._$pushCSSText(o),
            h = r._$add(a),
            t;
        i._$$Suggest = _._$klass();
        t = i._$$Suggest._$extend(u._$$Abstract);
        t.__init = function() {
            this.__sopt = {
                onchange: this.__onChange._$bind(this),
                onselect: this.__onSelect._$bind(this)
            };
            this.__super()
        };
        t.__reset = function(t) {
            this.__super(t);
            this.__sopt.autofill = 0 != t.autofill;
            this.__sopt.input = e._$get(t.input);
            this.__sopt.input.insertAdjacentElement("afterEnd", this.__body);
            this.__suggest = s._$$Suggest._$allocate(this.__sopt)
        };
        t.__destroy = function() {
            if (this.__suggest) {
                this.__suggest._$recycle();
                delete this.__suggest
            }
            this.__super();
            delete this.__sopt.input
        };
        t.__initXGui = function() {
            this.__seed_css = l
        };
        t.__initNode = function() {
            this.__super();
            this.__sopt.body = this.__body
        };
        t.__onChange = function(t) {
            this._$dispatchEvent("onchange", t)
        };
        t.__onSelect = function(t, e) {
            this._$dispatchEvent("onselect", t, e)
        };
        t._$setList = function(t, i) {
            if (c._$isArray(t)) t = r._$get(h, {
                xlist: t
            });
            this.__body.innerHTML = t || "";
            this.__suggest._$setList(!i ? e._$getChildren(this.__body) : e._$getByClassName(this.__body, i))
        };
        if (!0) n.copy(n.P("nej.ui"), i);
        return i
    },
    15, 1, 2, 4, 61, 79, 22, 80, 81);
I$(58,
    function(c, o, e, i, r, _, n, s, a, f, h, l) {
        var t, u = /^[\w-\.@]*$/i;
        a._$$Input = c._$klass();
        t = a._$$Input._$extend(_._$$EventTarget);
        t.__init = function(t) {
            this.__super(t)
        };
        t.__destroy = function() {
            this.__focusTimeout = clearTimeout(this.__focusTimeout);
            this.__blurTimeout = clearTimeout(this.__blurTimeout);
            i._$clearEvent(this.__input);
            i._$clearEvent(this.__label);
            this.__super()
        };
        t.__reset = function(t) {
            this.__super(t);
            this.__input = t.node;
            this.__form = t.form;
            this.__inputBox = n._$getParent(this.__input, "inputbox");
            this.__clearBtn = e._$getByClassName(this.__inputBox, "u-tip")[0];
            this.__needClose = t.needClose;
            this.__isUsername = t.isUsername;
            this.__domain = t.domain;
            this.__isPwd = t.isPwd;
            this.__enterNode = t.enterNode;
            this.__label = e._$getByClassName(this.__inputBox, "u-label")[0];
            var i = this.__supportPH();
            var r = e._$dataset(this.__input, "placeholder");
            if (this.__label) if (!i) e._$delClassName(this.__label, "f-dn");
            else {
                e._$addClassName(this.__label, "f-dn");
                e._$attr(this.__input, "placeholder", r)
            }
            this.__initEvent();
            if (this.__isUsername) this.__initSuggest()
        };
        t.__initEvent = function() {
            var t = [[this.__label, "click", this.__doFocus._$bind(this)], [this.__input, "focus", this.__onFocus._$bind(this)], [this.__input, "blur", this.__onBlur._$bind(this)], [this.__input, "input", this.__onInput._$bind(this, 0)], [this.__input, "keyup", this.__doEnter._$bind(this)]];
            if (this.__needClose) t.push([this.__clearBtn, "click", this._$onClear._$bind(this, 2)]);
            if (this.__isPwd);
            this.__doInitDomEvent(t)
        };
        t.__supportPH = function() {
            var t = o._$KERNEL;
            if ("trident" == t.engine && parseInt(t.release, 10) <= 5) return 0;
            else return 1
        };
        t.__doEnter = function(t) {
            var e = i._$getElement(t),
                n = e.name;
            if ("password" == n && 13 != t.keyCode) this._$dispatchEvent("onPwdKeyUp");
            if (13 == t.keyCode) i._$dispatchEvent(this.__enterNode, "click")
        };
        t.__onEye = function(i) {
            if (e._$hasClassName(this.__clearBtn, "eyeactive")) {
                var t = this.__pwdtext.value.trim();
                if (!/[\u4e00-\u9fa5]+/.test(t)) {
                    e._$delClassName(this.__clearBtn, "eyeactive");
                    e._$setStyle(this.__pwdtext, "zIndex", -1);
                    this.__input.value = t;
                    this.__input.focus()
                } else n._$showError(this.__input, "密码不能是中文！", "nerror")
            } else {
                var t = this.__input.value.trim();
                this.__setPwdText(t);
                e._$addClassName(this.__clearBtn, "eyeactive");
                e._$setStyle(this.__pwdtext, "zIndex", 1);
                this.__pwdtext.focus()
            }
        };
        t.__setPwdText = function(t) {
            this.__pwdtext.value = t
        };
        t.__doFocus = function() {
            this.__input.focus()
        };
        t.__onFocus = function() {
            if (this.__blurTimeout) this.__blurTimeout = clearTimeout(this.__blurTimeout);
            this._$dispatchEvent("onClearInptTimeout", this.__input);
            this.__focusTimeout = setTimeout(function() {
                e._$replaceClassName(this.__inputBox, "error-color", "active");
                if ("password" == this.__input.id && !this.__firstFocus) {
                    this.__input.value = "";
                    this.__firstFocus = 1
                }
                this._$dispatchEvent("onfocus", this.__input)
            }._$bind(this), 100)
        };
        t.__onBlur = function(t) {
            if (this.__focusTimeout) this.__focusTimeout = clearTimeout(this.__focusTimeout);
            this._$dispatchEvent("onClearInptTimeout", this.__input);
            this.__blurTimeout = setTimeout(function() {
                var t = this.__form._$checkValidity(this.__input);
                e._$delClassName(this.__inputBox, "active");
                this._$dispatchEvent("onstate", t, this.__input)
            }._$bind(this), 100)
        };
        t.__onInput = function(n, r) {
            var i = this.__input;
            var t = (i.value || "").length;
            if (this.__needClose || this.__isPwd) e._$setStyle(this.__clearBtn, "display", t > 0 ? "block": "none");
            if (this.__isPwd);
            if (t > 0) this.__label.style.display = "none";
            else if (0 == t) this.__label.style.display = "block";
            this._$dispatchEvent("onInput", i)
        };
        t.__initSuggest = function() {
            var t = function(e, i, t) {
                t = t ? " " + t: "";
                return '<div class="itm' + t + '" data-value=' + e + i + ">" + e + i + "</div>"
            };
            var i = function(n) {
                var i = [];
                if (u.test(n)) {
                    if ( - 1 == n.indexOf("@")) r._$forEach(this.__suffix,
                        function(e) {
                            var r = t(n, e);
                            i.push(r)
                        });
                    else {
                        var s = n.split("@");
                        _firstValue = s[0],
                            _lastValue = s[1];
                        if (n.match(/@/g).length > 1) i = [];
                        else if (!_lastValue) r._$forEach(this.__suffix,
                            function(e) {
                                e = e.split("@")[1];
                                var r = t(n, e);
                                i.push(r)
                            });
                        else if (_lastValue.indexOf(".com") > -1) i = [];
                        else r._$forEach(this.__suffix,
                                function(e) {
                                    if (1 == e.indexOf(_lastValue)) {
                                        var n = t(_firstValue, e);
                                        i.push(n)
                                    }
                                })
                    }
                    if (i[0]) i[0] = i[0].replace('class="itm"', 'class="itm js-selected"');
                    this.__suggest._$setList(i.join(""));
                    e._$get("account-box").style.zIndex = "500"
                } else this.__suggest._$setList(i.join(""))
            };
            var a = function(t, i) {
                if (!window.outlinkflag) {
                    e._$get("account-box").style.zIndex = "0";
                    this.__suggest._$setList([]);
                    this._$dispatchEvent("onClearEmailTimeout");
                    if (!this.__checkIpt(t, "email")) n._$showError(this.__input, "帐号格式错误", "nerror");
                    else n._$removeError(this.__input, "nerror");
                    this._$dispatchEvent("onFocusNext")
                }
            };
            return function() {
                this.__suffix = this.__suffix || ["@163.com", "@126.com", "@yeah.net", "@188.com", "@vip.163.com", "@vip.126.com"];
                if (this.__suggest) this.__suggest = s._$$Suggest._$recycle(this.__suggest);
                var t = n._$getParent(this.__input, "u-input");
                if (!this.__suggest) this.__suggest = s._$$Suggest._$allocate({
                    parent: t,
                    clazz: "m-sug",
                    input: this.__input,
                    autofill: !1,
                    onchange: i._$bind(this),
                    onselect: a._$bind(this)
                });
                window.matchMedia
            }
        } ();
        t.__checkIpt = function() {
            var t = {
                email: /^[\w-\.]+@(?:[\w-]+\.)+[a-z]{2,6}$/i
            };
            return function(e, i) {
                e = e.trim();
                return t[i].test(e)
            }
        } ();
        t._$showCloseBtn = function() {
            e._$setStyle(this.__clearBtn, "display", "block")
        };
        t._$hideCloseBtn = function() {
            e._$setStyle(this.__clearBtn, "display", "none")
        };
        t._$setSuggest = function(t) {
            this.__suffix = t;
            this.__initSuggest()
        };
        t._$hideLabel = function() {
            this.__label.style.display = "none"
        };
        t._$onClear = function(t) {
            this.__input.value = "";
            this.__clearBtn.style.display = "none";
            this.__label.style.display = "block";
            if ("2" == t) try {
                this.__input.focus()
            } catch(e) {}
            i._$stop(t);
            this._$dispatchEvent("onInput", this.__input)
        }
    },
    1, 33, 2, 3, 4, 5, 11, 63);
I$(32,
    function(e, i, n, r) {
        var t = {
            404 : "网络异常，请刷新页面重试",
            "-1": "网络不好，请刷新页面重试",
            "-2": "网络不好，请刷新页面重试",
            0 : "网络不好，请刷新页面重试",
            401 : "操作超时，请刷新页面重试",
            402 : "当前网络异常，请检查您的网络环境",
            403 : "当前网络环境不安全，请稍后再试!",
            410 : "服务IP限制",
            441 : "验证码输入错误",
            444 : "请滑动滑块验证码",
            411 : "IP一分钟内登录不存在的用户达到限制，5分钟后再试",
            413 : "帐号或密码错误",
            "412-01": "您登录错误次数过多，请稍后再试",
            "412-02": "您登录错误次数过多，请明天再试",
            "413-01": "您登录错误密码次数过多，请稍后再试",
            "413-02": "您登录错误密码次数过多，请明天再试",
            "413-03": "您的IP登录错误密码次数过多，请稍后再试",
            "414-01": "您的IP登录错误次数过多，请稍后再试",
            "414-02": "您的IP登录错误次数过多，请明天再试",
            416 : "您的IP登录过于频繁,请稍后再试",
            "417-01": "您的IP登录成功次数过多，请稍后再试",
            "417-02": "您的IP登录成功次数过多，请明天再试",
            "418-01": "您登录成功次数过多,请稍后再试",
            "418-02": "您登录成功次数过多,请明天再试",
            "419-01": "您登录过于频繁,请稍后再试",
            "419-02": "您的IP登录过于频繁,请稍后再试",
            422 : "帐号被锁定，您可以通过【网易用户中心】易信版或微信版解锁,或24小时后再试。如为被盗，申请<a target='_blank' href='https://mima.163.com/nie/' style='color:#fa5b5b;text-decoration:underline;'>帐号申诉</a>",
            420 : "帐号不存在",
            424 : "该靓号服务已到期，<a target='_blank' href='http://haoma.163.com/pay/pay.do?ayRenew=1'>点击续费</a>，更多精彩",
            425 : "该帐号尚未激活，请到已注册的邮箱<a target='_blank' href='#'>激活帐号</a>",
            426 : "该帐号未及时激活，请重新注册",
            442 : "验证码输入错误",
            443 : "请输入正确的短信验证码",
            409 : "您登录过于频繁,请稍后再试",
            500 : "系统繁忙",
            602 : "邮箱服务已到期，<a target='_blank' href='http://vpay.vip.163.com/vippayunion/index.html' style='color:#fa5b5b;text-decoration:underline;'>点击续费</a>，更多精彩"
        };
        return t
    });
I$(30,
    function(_, e, r, c, u, l, f, d, i, p, s, h, a, g, v, m) {
        var t, o, n = {
            email: /^[\w-\.]+@(?:[\w-]+\.)+[a-z]{2,6}$/i,
            sms: /^[0-9a-zA-Z]{4}$/,
            pwd: /^[0-9a-zA-Z]{6,16}$/
        };
        a._$$Login = _._$klass();
        t = a._$$Login._$extend(f._$$Module);
        t.__init = function(t) {
            this.__setTimeoutList = {};
            this.__opts = t.opts || {};
            this.__domainSuffixs = this.__opts.domainSuffixs || "";
            this.__lazyCheck = this.__opts.lazyCheck || 0;
            this.__domainSuffixs = this.__domainSuffixs ? this.__domainSuffixs.split(",") : [];
            this.__placeholder = this.__opts.placeholder || {};
            this.__domain = this.__opts.prdomain || "";
            this.__swidth = this.__opts.swidth || 320;
            this.__forgetpwdlink = this.__opts.forgetpwdlink || "http://reg.163.com/resetpwd/index.do";
            this.__unLoginText = this.__opts.unLoginText || "十天内免登录";
            this.__server = this.__opts.server || "captcha.reg.163.com/v1_5";
            this.__productkey = this.__opts.productkey || "8infaddo36mtuzseml0wohs6omu4v5lo";
            this.__slideOpt = {
                server: this.__server,
                productKey: this.__productkey,
                width: this.__swidth,
                alignToSpace: !0,
                hintTxt: "按住滑块，拖动完成上方拼图",
                ie7Downgrade: !0,
                verifyCallback: this.__slideVerify._$bind(this),
                initCallback: this.__initCallback._$bind(this),
                initErrorHandler: this.__initErrorHandler._$bind(this)
            };
            this.__errMsg = this.__opts.errMsg || "";
            this.__gotoRegText = this.__opts.gotoRegText || "去注册";
            this.__super()
        };
        t.__slideVerify = function(t) {
            if (this.__lazyCheck) if (t && t.value) this.__cbVftcp();
            else this.__cbVftcpEx();
            else if (t && t.value) this._$verifyCap();
            else this.__cbVftcpEx()
        };
        t.__reset = function(t) {
            this.__ipts = [];
            this.__product = this.__opts.product || "";
            this.__pkid = this.__opts.promark || "";
            this.__super(t);
            this._$loginTime();
            this.__imgLock = 0;
            this.__states["email"] = 1;
            this.__states["password"] = 1;
            this._$hideCheckCode();
            this.codeTryTime = 0
        };
        t.__destroy = function() {
            this.__initSlideCap = null;
            this.__tvlTime = clearInterval(this.__tvlTime);
            this.__super()
        };
        t.__initNode = function() {
            this.__super();
            this.__checkCode = e._$getByClassName(this.__body, "ckbox")[0];
            this.__slideCapBox = e._$getByClassName(this.__body, "slidebox")[0];
            this.__cdImg = e._$getByClassName(this.__body, "ckimg")[0];
            this.__olist = e._$getByClassName(this.__body, "olist")[0];
            this.__cdImgLink = e._$getByClassName(this.__body, "j-cklink")[0];
            this.__loginBtn = e._$getByClassName(this.__body, "u-loginbtn")[0]
        };
        t.__initXGui = function() {
            var t = this.__parseOauth();
            o = u._$addNodeTemplate(l._$get("login-tmp", {
                config: t || [],
                gotoRegText: this.__gotoRegText,
                unLoginText: this.__unLoginText,
                forgetpwdlink: this.__forgetpwdlink
            }));
            this.__seed_html = o
        };
        t.__parseOauth = function() {
            return i._$parseOauth()
        };
        t.__reLoginTime = function() {
            this.__ipts[1]._$onClear(1);
            setTimeout(function() {
                if (this.__nameinput.value) i._$showError(this.__passwordinput, "请重新输入密码", "nerror");
                this._$loginTime()
            }._$bind(this), 100)
        };
        t.__initEvent = function() {
            this.__inputs = e._$getByClassName(this.__body, "j-inputtext");
            this.__nameinput = this.__inputs[0];
            this.__passwordinput = this.__inputs[1];
            this.__smscode = this.__inputs[2];
            var t;
            if (0 == this.__ipts.length) {
                this.__setPlaceHolder();
                c._$forEach(this.__inputs,
                    function(n, i) {
                        var e = {
                            node: n,
                            form: this.__form,
                            needClose: 1,
                            onfocus: this.__onFocus._$bind(this),
                            onInput: this.__onInput._$bind(this),
                            onPwdKeyUp: this.__onPwdKeyUp._$bind(this),
                            onFocusNext: this.__onFocusNext._$bind(this),
                            onClearInptTimeout: this.__onClearInptTimeout._$bind(this)
                        };
                        if (!i) {
                            e.isUsername = this.__domain ? 0 : 1;
                            e.domain = this.__domain
                        }
                        if (1 == i) e.isPwd = 1;
                        t = p._$$Input._$allocate(e);
                        if (!i && this.__domainSuffixs && this.__domainSuffixs.length > 0) t._$setSuggest(this.__domainSuffixs);
                        this.__ipts.push(t)
                    }._$bind(this))
            }
            this.__doInitDomEvent([[this.__cdImg, "click", this._$getCheckCode._$bind(this)], [this.__cdImgLink, "click", this._$getCheckCode._$bind(this)], [this.__olist, "click", this._$doThirdLogin._$bind(this)]])
        };
        t._$doThirdLogin = function(t) {
            i._$doThirdLogin(t)
        };
        t.__onPwdKeyUp = function() {
            this._$dispatchEvent("onPwdKeyUp")
        };
        t.__onFocusNext = function() {
            this._stopEnter = 1;
            this.__inputs[1].focus()
        };
        t.__onFocus = function(t) {
            i._$removeError(t, "nerror")
        };
        t.__onClearInptTimeout = function(e) {
            var t = e.name;
            if (this.__setTimeoutList["invalid" + t]) this.__setTimeoutList["invalid" + t] = clearTimeout(this.__setTimeoutList["invalid" + t]);
            if (this.__setTimeoutList["valid" + t]) this.__setTimeoutList["valid" + t] = clearTimeout(this.__setTimeoutList["valid" + t])
        };
        t.__initForm = function() {
            if (!this.__form) this.__form = d._$$WebForm._$allocate({
                form: "login-form",
                domain: this.__domain || null,
                oninvalid: function(i) {
                    var t = "请输入",
                        n = i.code,
                        a = r._$getElement(i),
                        s = a.name;
                    if (!window.outlinkflag) {
                        this.__clearTimeout(s);
                        if ("checkcode" == s && e._$hasClassName(this.__checkCode, "f-dn") && this.__needSlideCap) i.ignore = 1;
                        this.__setTimeoutList["invalid" + s] = setTimeout(function(s) {
                            var r = s;
                            if ("checkcode" != r || !e._$hasClassName(this.__checkCode, "f-dn")) if ("slidecap" != r || e._$hasClassName(this.__slideCapBox, "f-dn")) {
                                if ( - 1 == n) {
                                    if ("email" == r) t += "帐号";
                                    else if ("password" == r) t += "密码";
                                    else if ("checkcode" == r) t = "验证码输入错误"
                                } else if ( - 4 == n || -2 == n || -3 == n) {
                                    t = "格式错误";
                                    if ("email" == r) {
                                        t = "帐号" + t;
                                        this.__ipts[1]._$onClear()
                                    } else if ("password" == r) t = "密码" + t;
                                    else if ("checkcode" == r) t = "验证码输入错误"
                                }
                                if ("slidecap" == r && e._$hasClassName(this.__slideCapBox, "f-dn")) this.__states[r] = 0;
                                else this.__states[r] = 1;
                                if ( - 1 != n) this.__checkList(a, t)
                            } else if (!this.__vSlide()) this.__states["slidecap"] = 0;
                            else this.__states["slidecap"] = 1;
                            else {
                                if (this.__needSlideCap) i.ignore = 1;
                                this.__states[r] = 0
                            }
                        }._$bind(this, s), 100);
                        i.stopped = !0
                    } else if (!this.__refocus && 1 == window.outlinkflag) this.__refocus = setTimeout(function() {
                        this.__refocus = clearTimeout(this.__refocus);
                        a.focus()
                    }._$bind(this), 200)
                }._$bind(this),
                onvalid: function(i) {
                    var n = r._$getElement(i),
                        t = n.name;
                    this.__clearTimeout(t);
                    this.__setTimeoutList["valid" + t] = setTimeout(function(t) {
                        var i = t;
                        this.__states[i] = 0;
                        var r = e._$get("nerror");
                        this.__hideErrorList(n)
                    }._$bind(this, t), 100);
                    i.stopped = !0
                }._$bind(this)
            })
        };
        t.__clearTimeout = function(t) {
            if (this.__setTimeoutList["invalid" + t]) this.__setTimeoutList["invalid" + t] = clearTimeout(this.__setTimeoutList["invalid" + t]);
            if (this.__setTimeoutList["valid" + t]) this.__setTimeoutList["valid" + t] = clearTimeout(this.__setTimeoutList["valid" + t])
        };
        t.__hideErrorList = function(t) {
            if (this.__checkStatus(t)) i._$removeError(t, "nerror")
        };
        t.__checkStatus = function(t, i) {
            var n = t.name;
            if ("email" == n) {
                if (!i) {
                    var r = -1 != t.value.indexOf("@") ? t.value.substring(t.value.indexOf("@")).toLocaleLowerCase() : this.__domain;
                    if (!r) r = t.value;
                    _gaq.push(["_trackEvent", "登录步骤", "【1】帐号输入", "输入成功：" + r])
                }
            } else if ("password" == n) {
                if (!i) _gaq.push(["_trackEvent", "登录步骤", "【2】密码输入", "输入" + (t.value || "").length + "位密码"]);
                if (this.__states["email"]) return
            } else if ("checkcode" == n && !e._$hasClassName(this.__checkCode, "f-dn")) {
                if (!i) {
                    this.codeTryTime = this.codeTryTime ? this.codeTryTime + 1 : 1;
                    _gaq.push(["_trackEvent", "登录步骤", "【3】验证码输入", "图片验证码校验成功,尝试次数：" + this.codeTryTime])
                }
                if (this.__states["password"] || this.__states["email"]) return
            }
            return 1
        };
        t.__checkList = function(t, e) {
            if (this.__checkStatus(t, 1)) i._$showError(t, e, "nerror");
            else i._$showError2(t, e, "nerror", 1)
        };
        t.__onInput = function(t) {
            setTimeout(this.__checkNextBtn._$bind(this, t), 50)
        };
        t.__checkNextBtn = function(i) {
            var t = this.__vName();
            var e = this.__vPwd();
            var s = this.__vSms();
            var a = this.__vSlide();
            if (i && "checkcode" == i.name) {
                var r = this.__smscode.value.trim();
                if (n["sms"].test(r) && !this.__lazyCheck) this.__doCheckSmsCode(r)
            }
            if (!this.__needSlideCap && !this.__needCheckCode) if (!t && !e) this._$dispatchEvent("ondisabled", 0);
            else this._$dispatchEvent("ondisabled", 1);
            else if (this.__needSlideCap) if (!t && !e && !a) this._$dispatchEvent("ondisabled", 0);
            else this._$dispatchEvent("ondisabled", 1);
            else if (this.__needCheckCode) if (!t && !e && !s) this._$dispatchEvent("ondisabled", 0);
            else this._$dispatchEvent("ondisabled", 1)
        };
        t.__vSms = function() {
            var t = this.__smscode.value.trim();
            if (n["sms"].test(t)) return 0;
            else return 1
        };
        t.__doCheckSmsCode = function(t) {
            if (!this.__checkSmsCodeLock) {
                this.__checkSmsCodeLock = 1;
                var e = {
                    cap: t,
                    pd: this.__product,
                    pkid: this.__pkid
                };
                s._$request("checkSmsCode", e, this.__cbSmsCode._$bind(this), this.__ckSmsCodeEx._$bind(this, "验证码输入错误"), 1, this.__product)
            }
        };
        t.__cbSmsCode = function(t) {
            this.__checkSmsCodeLock = 0;
            this.__imgLock = 1;
            this.__smscode.disabled = !0;
            this.__ipts[2]._$hideCloseBtn();
            e._$getByClassName(document, "u-tip")[3].style.display = "block";
            i._$removeError(this.__smscode, "nerror")
        };
        t.__ckSmsCodeEx = function(t, n) {
            var e = n.ret;
            this.codeTryTime = this.codeTryTime ? this.codeTryTime + 1 : 1;
            if ("441" == e) {
                this.__needSlideCap = 0;
                this.__needCheckCode = 1;
                this._$refreshCheckCode();
                if (1 != t) i._$showError(this.__smscode, t, "nerror");
            } else if ("444" == e) {
                this.__needSlideCap = 1;
                this.__needCheckCode = 0;
                this._$refreshCheckCode();
                if (1 != t) i._$showError(this.__smscode, t, "nerror")
            } else {
                this.__checkSmsCodeLock = 0;
                this._$getCheckCode();
                this.__ipts[2]._$onClear();
                this._$dispatchEvent("ondisabled", 1);
                t = h[e] || "验证码输入错误";
                if ("401" == e) t = "请刷新页面重试";
                i._$showError(this.__smscode, t, "nerror")
            }
        };
        t.__vName = function() {
            var t = this.__nameinput.value.trim();
            if ("" !== t) return 0;
            else return 1
        };
        t.__vPwd = function() {
            var t = this.__passwordinput.value.trim();
            if ("" !== t) return 0;
            else return 1
        };
        t._$setUsername = function(t) {
            try {
                this.__inputs[0].value = t;
                if (!this.__errMsg);
                this.__ipts[0]._$showCloseBtn();
                if ("" !== this.__inputs[0].value && !this.__errMsg);
            } catch(e) {}
        };
        t._$showCheckCode = function() {
            var t = e._$get("cnt-box-parent");
            this.__checkSmsCodeLock = 0;
            this._$hideCheckCode();
            this.__states["checkcode"] = 1;
            this.__needCheckCode = 1;
            e._$delClassName(this.__checkCode, "f-dn");
            this.__imgLock = 0;
            this.__smscode.disabled = !1;
            this._$getCheckCode();
            this.__ipts[2]._$onClear();
            this._$dispatchEvent("ondisabled", 1);
            this.__checkNextBtn();
            e._$addClassName(t, "hascheckcode");
            setTimeout(function() {
                    i._$resize()
                },
                200)
        };
        t._$doFocus = function(t) {
            if ("pwd" == t) this.__passwordinput.focus();
            if ("username" == t) this.__nameinput.focus()
        };
        t._$getCheckCode = function(t) {
            if (!this.__imgLock) {
                this.__cdImg.src = MP.getCaptchaLogin(this.__product, this.__pkid, window["$cookieDomain"]);
                e._$getByClassName(document, "u-tip")[3].style.display = "none"
            }
        };
        t._$hideLabel = function() {
            this.__ipts[0]._$hideLabel()
        };
        t.$clearText = function(t) {
            t.value = ""
        };
        t._$clearLoginTime = function() {
            this.__tvlTime = clearInterval(this.__tvlTime)
        };
        t._$loginTime = function() {
            return 1
        };
        t._$verifyCap = function() {
            if (!this.__slideCapLock) {
                this.__slideCapLock = 1;
                var t = e._$get("pwd").value;
                var i = {
                    cap: t,
                    pd: this.__product,
                    pkid: this.__pkid,
                    capkey: this.__productkey
                };
                s._$request("vftcp", i, this.__cbVftcp._$bind(this), this.__cbVftcpEx._$bind(this), 1)
            }
        };
        t._$getSmsValue = function() {
            return this.__smscode.value
        }
    },
    1, 2, 3, 4, 6, 22, 60, 57, 11, 58, 21, 32);
I$(27,
    function(c, e, s, a, _, n, i, o, u, r, h, l, f) {
        var t;
        r._$$Manager = c._$klass();
        t = r._$$Manager._$extend(_._$$EventTarget);
        t.__init = function(t) {
            this.__product = t.product;
            this.__promark = t.promark;
            this.__super(t)
        };
        t.__reset = function(t) {
            this.__super(t);
            window.$outLoginKey = this.__options.outLoginKey || "";
            this.__box = e._$get("cnt-box");
            this.__box2 = e._$get("cnt-box2");
            n._$render(this.__box, "h-tmp");
            this.__initEvent()
        };
        t.__destroy = function() {
            this.__super()
        };
        t.__initEvent = function() {
            this.__doInitDomEvent([[document, "click", this.__doAction._$bind(this)], [document, "mouseover", this.__onMouseover._$bind(this)]])
        };
        t.__checkCookie = function() {
            return;
            var t
        };
        t.__onMouseover = function(t) {
            var i = s._$getElement(t),
                n = e._$dataset(i, "outlink") || 0;
            window.outlinkflag = n
        };
        t.__changePage = function(t) {
            e._$setStyle(this.__box, "display", t ? "none": "block");
            e._$setStyle(this.__box2, "display", t ? "block": "none")
        };
        t.__showFail = function(t) {
            o._$showFail(t)
        };
        t.__showFail2 = function(t) {
            o._$showFail(t)
        };
        t.__showLeak = function(t) {
            this.__isLeak = !0;
            if (1 == t) {
                this.__isLeak1 = !0;
                n._$render(this.__box2, "exception1-tmp", {
                    product: this.__product,
                    promark: this.__promark
                })
            } else if (2 == t) {
                this.__isLeak2 = !0;
                n._$render(this.__box2, "exception2-tmp", {
                    product: this.__product,
                    promark: this.__promark
                })
            } else if (3 == t) {
                this.__isLeak3 = !0;
                n._$render(this.__box2, "exception3-tmp", {
                    product: this.__product,
                    promark: this.__promark
                })
            }
            this.__changePage(1)
        };
        t.__sendSize = function(r) {
            var t = document.body.scrollWidth,
                e = document.body.clientHeight,
                n = {
                    width: t,
                    height: e,
                    type: r || "resize"
                };
            if (t * e > 0) {
                n["URS-CM"] = 1;
                i._$postMessage("_parent", {
                    data: n
                })
            }
        };
        t.__sendClose = function(r) {
            var s = e._$getByClassName(document, "j-inputtext");
            var t = !0;
            a._$forEach(s,
                function(e, i) {
                    if (e.value) t = !1
                });
            if (r || this.__islogin || t) {
                var n = {
                    type: "close"
                };
                n["URS-CM"] = 1;
                i._$postMessage("_parent", {
                    data: n
                })
            } else this.__showConfirm()
        };
        t.__doAction = function(r) {
            var a = s._$getElement(r),
                t = e._$dataset(a, "action");
            if ("confirmgoon" == t) {
                e._$addClassName(e._$get("confirm"), "f-dn");
                e._$delClassName(e._$get("cnt-box-parent"), "f-dn");
                this.__sendSize("init")
            }
            var o = {
                pid: this.__promark,
                pdt: this.__product
            };
            if ("confirmclose" == t) {
                _gaq.push(["_trackEvent", "注册步骤", "【-】放弃注册", "放弃注册"]);
                var n = {
                    type: "close"
                };
                n["URS-CM"] = 1;
                i._$postMessage("_parent", {
                    data: n
                });
                this.__closeFlag = !1
            }
        };
        t.__showConfirm = function() {
            e._$addClassName(e._$get("cnt-box-parent"), "f-dn");
            e._$delClassName(e._$get("confirm"), "f-dn");
            this.__sendSize("init")
        };
        t.__addIframe = function(i, t, n) {
            if ("https:" == location.protocol) t = t.replace("http:", "https:");
            var r = e._$createXFrame({
                src: t,
                parent: document.body,
                visible: !1,
                onload: function() {
                    this.__iframeIndex++;
                    if (this.__ifarmeSize == this.__iframeIndex) {
                        this.__iframeCt = clearTimeout(this.__iframeCt);
                        this.__sendMsg(i)
                    }
                }._$bind(this)
            })
        };
        t.__setDomains = function(i) {
            var t = i || {};
            t["URS-CM"] = 1;
            this.__iframeIndex = 0;
            var e = t.nextUrls || [];
            this.__ifarmeSize = e.length || 0;
            if (this.__ifarmeSize > 0) this.__iframeCt = setTimeout(function() {
                this.__sendMsg(t)
            }._$bind(this), 5e3);
            else this.__sendMsg(t);
            a._$forEach(e, this.__addIframe._$bind(this, t))
        };
        t.__sendMsg = function(e) {
            var t = e || {};
            t["URS-CM"] = 1;
            if (window.$outLoginKey) t.fromOutLogin = 1;
            i._$postMessage("_parent", {
                data: t
            })
        };
        window.thirdHandler = function(e) {
            var t = {
                type: "success",
                isOther: !0,
                username: e
            };
            t["URS-CM"] = 1;
            if (window.$outLoginKey) t.fromOutLogin = 1;
            i._$postMessage("_parent", {
                data: t
            })
        };
        window.$outLogin = function(t) {
            var e;
            if (t.isOther) window.thirdHandler(t.username);
            else {
                var n = JSON.stringify(t);
                e = JSON.parse(n);
                e.toOpener = 1;
                i._$postMessage("_parent", {
                    data: e
                })
            }
        };
        return r
    },
    1, 2, 3, 4, 5, 22, 10, 11, 29);
I$(67,
    function(h, e, f, o, n, l, i, t, _, c, u) {
        var r = "https://reg.163.com/services/getqrcodeid";
        var s = "https://reg.163.com/services/getUrlQrcode";
        var a = "https://reg.163.com/services/qrcodeauth";
        var d = "https://reg.163.com/services/ticketloginForZJ";
        t._$initQr = function(e) {
            if (!e) e = [];
            this.__totalState = 0;
            this.__ticket = "";
            this.__domain = "";
            this.__domains = e.domains || "";
            this.__prdomain = e.prdomain || "";
            this.__product = e.product || "urs";
            this.__usage = e.usage || 0;
            this.__size = e.size || "200";
            this.__format = e.format || "png";
            this.__qrcodeDom = e.qrcodeDom || null;
            this.__imgDom = e.imgDom || null;
            this.__oWarmDom = e.oWarmDom || null;
            this.__qrSuccDom = e.qrSuccDom || null;
            this.__qrBackBrn = e.qrBackDom || null;
            this.__pollingSec = e.pollingSec || 2e3;
            this.__maxPollingTimes = e.maxPollingTimes || 150;
            this.__completePollingTimes = e.completePollingTimes || 60;
            this.__qrLoginSucc = e.qrLoginSucc || this.__qrLoginSucc;
            this.__qrLoginErr = e.qrLoginErr || this.__qrLoginErr;
            this.__confirmLogin = e.confirmLogin || this.__confirmLogin;
            this.__scanIsComplete = e.scanIsComplete || this.__scanIsComplete;
            this.__codeLose = e.codeLose || this.__codeLose;
            this.__oWarmDom.onclick = function() {
                t.__changeState(1)
            };
            this.__qrBackBrn.onclick = function() {
                t.__changeState(1)
            };
            this.__changeState(1)
        };
        t.__changeState = function(t) {
            switch (t) {
                case 0:
                    this._$clearAllStatus();
                    this.__hideScanSucc();
                    this.__showQrcodeM();
                    this.__showOWarm();
                    this.__totalState = 0;
                    break;
                case 1:
                    this.__hideScanSucc();
                    this.__showQrcodeM();
                    this.__getQrcode();
                    this.__hideOWarm();
                    this.__totalState = 1;
                    break;
                case 2:
                    this.__hideQrcodeM();
                    this.__showScanSucc();
                    this.__totalState = 2
            }
        };
        t.__getQrcode = function() {
            var t = this;
            if (1 !== this.__totalState) {
                var e = function(e) {
                    e = JSON.parse(e["content"]);
                    t.__qrId = e["l"]["i"];
                    if (t.__qrId) t.__showQrcode(t.__imgDom)
                };
                var n = {
                    product: this.__product,
                    usage: this.__usage
                };
                i._$requestJsonp(r, n, e)
            }
        };
        t.__showQrcode = function(t) {
            t.src = s + "?uuid=" + this.__qrId + "&size=" + this.__size + "&format=" + this.__format + "&" + (new Date).getTime();
            this.__restartTiming(0)
        };
        t.__restartTiming = function(t) {
            that = this;
            this.__qrTiming = 0;
            var e = this.__pollingSec;
            var i = 0 == t ? this.__maxPollingTimes: this.__completePollingTimes;
            if (this.__checkQrStIntv) clearInterval(that.__checkQrStIntv);
            this.__checkQrStIntv = setInterval(function() {
                    that.__qrTiming++;
                    that.__checkQrStatus();
                    if (that.__qrTiming >= i) {
                        clearInterval(that.__checkQrStIntv);
                        that.__changeState(0);
                        that.__codeLose()
                    }
                },
                e)
        };
        t.__checkQrStatus = function() {
            var t = this;
            var e = function(e) {
                var i = e.retCode;
                switch (i) {
                    case "200":
                        if (0 == t.__totalState) break;
                        t.__changeState(2);
                        t.__ticket = e.ticket;
                        t.__domain = e.domain;
                        t.__confirmLogin(e);
                        t._$clearAllStatus();
                        t.__doQrLogin();
                        break;
                    case "401":
                        if (1 != t.__totalState) break;
                        t.__changeState(0);
                        t.__codeLose(e);
                        break;
                    case "404":
                        if (1 != t.__totalState) break;
                        t.__changeState(0);
                        t.__codeLose(e);
                        break;
                    case "408":
                        break;
                    case "409":
                        if (1 != t.__totalState) break;
                        t.__scanIsComplete(e);
                        t.__changeState(2);
                        t.__restartTiming(1);
                        break;
                    case "500":
                        if (1 != t.__totalState) break;
                        t.__codeLose(e);
                        t.__changeState(0)
                }
            };
            var n = {
                uuid: this.__qrId,
                product: this.__product
            };
            i._$requestJsonp(a, n, e)
        };
        t.__doQrLogin = function() {
            var t = this;
            var e = function(e) {
                var n = e.ret;
                if ("201" == n) {
                    var i = e.username; - 1 === i.indexOf("@") ? i += "@163.com": null;
                    if (!t.__prdomain || i.substring(i.indexOf("@")) == t.__prdomain) t.__qrLoginSucc(i, e);
                    else {
                        t.__changeState(1);
                        var r = t.__prdomain ? "扫码失败，请使用" + t.__prdomain + "帐号扫码登录": "请使用指定域名帐号扫码登录";
                        t.__qrLoginErr(e, r)
                    }
                } else {
                    t.__changeState(0);
                    t.__qrLoginErr(e)
                }
            };
            var i = {
                tk: this.__ticket,
                pd: this.__product,
                domains: this.__domains
            };
            n._$request("qrlogin", i, e._$bind(this), e._$bind(this), 1)
        };
        t.__qrLoginSucc = function(t) {
            console.log(t)
        };
        t.__qrLoginErr = function(t) {
            console.log(t)
        };
        t.__confirmLogin = function() {
            console.log("用户已确认，请执行登录代码！")
        };
        t.__scanIsComplete = function() {
            console.log("扫描完成，等待用户确认！")
        };
        t.__codeLose = function() {
            console.log("二维码失效！")
        };
        t._$clearAllStatus = function() {
            clearInterval(this.__checkQrStIntv);
            this.__qrTiming = 0
        };
        t.__showQrcodeM = function() {
            if (this.__qrcodeDom) e._$delClassName(this.__qrcodeDom, "f-dn")
        };
        t.__hideQrcodeM = function() {
            if (this.__qrcodeDom) e._$addClassName(this.__qrcodeDom, "f-dn")
        };
        t.__showOWarm = function() {
            if (this.__oWarmDom) e._$delClassName(this.__oWarmDom, "f-dn")
        };
        t.__hideOWarm = function() {
            if (this.__oWarmDom) e._$addClassName(this.__oWarmDom, "f-dn")
        };
        t.__showScanSucc = function() {
            if (this.__qrSuccDom) e._$delClassName(this.__qrSuccDom, "f-dn")
        };
        t.__hideScanSucc = function() {
            if (this.__qrSuccDom) e._$addClassName(this.__qrSuccDom, "f-dn")
        };
        return t
    },
    1, 2, 3, 4, 21, 36, 11);
I$(31,
    function(o, e, p, h, _, c, a, r, m, s, u, n, l, f, d) {
        var t, i;
        n._$$QrcodeManager = o._$klass();
        t = n._$$QrcodeManager._$extend(a._$$Abstract);
        t.__init = function(t) {
            this.__product = t.product || "urs";
            this.__prdomain = t.prdomain || "";
            this.__toolName = t.toolName || "邮箱大师App";
            this.__toolUrl = t.toolUrl || "http://mail.163.com/dashi/?from=urs";
            this.__opts = t.opts;
            this.__super()
        };
        t.__reset = function(t) {
            this.__super(t);
            this.__qrBox = e._$getByClassName(document, "m-qrcode")[0];
            this.__tooApp.href = this.__toolUrl;
            this.__tooApp.innerHTML = this.__toolName;
            this.__qrcodeDom = e._$getByClassName(this.__qrBox, "j-qrblock")[0];
            this.__qrImgDom = e._$getByClassName(this.__qrBox, "j-qrcode")[0];
            this.__qrOWarmDom = e._$getByClassName(this.__qrBox, "j-owarm")[0];
            this.__qrSuccDom = e._$getByClassName(this.__qrBox, "j-qrsucc")[0];
            this.__qrBackDom = e._$getByClassName(this.__qrBox, "j-qrback")[0];
            var i = {
                domains: this.__opts.domains,
                product: this.__product,
                prdomain: this.__prdomain,
                qrcodeDom: this.__qrcodeDom,
                imgDom: this.__qrImgDom,
                oWarmDom: this.__qrOWarmDom,
                qrSuccDom: this.__qrSuccDom,
                qrBackDom: this.__qrBackDom,
                qrLoginSucc: this.__qrLoginSucc._$bind(this),
                qrLoginErr: this.__qrLoginErr._$bind(this),
                confirmLogin: this.__confirmLogin._$bind(this),
                scanIsComplete: this.__scanIsComplete._$bind(this),
                codeLose: this.__codeLose._$bind(this)
            };
            s._$initQr(i);
            r._$resize()
        };
        t.__destroy = function() {
            this.__super();
            s._$clearAllStatus()
        };
        t.__initNode = function() {
            this.__super();
            this.__tooApp = e._$getByClassName(this.__body, "j-toolapp")[0]
        };
        t.__initXGui = function() {
            i = _._$addNodeTemplate(c._$get("qrcode-tmp"));
            this.__seed_html = i
        };
        t.__qrLoginSucc = function(t, e) {
            this.__username = t;
            this.__doQrLoginSucc(e)
        };
        t.__doQrLoginSucc = function(t) {
            u._$cookie("THE_LAST_LOGIN", {
                value: this.__username,
                expires: 30
            });
            var e = t.nextUrls;
            var i = {
                type: "success",
                username: this.__username,
                isqr: 1,
                nextUrls: e
            };
            this._$dispatchEvent("sendmsg", i);
            _gaq.push(["_trackEvent", "二维码登录", "【3】二维码登录结果", "扫码登录成功"])
        };
        t.__qrLoginErr = function(e, t) {
            console.log(e);
            if (!e) e = [];
            var i = t || "扫码登录失败，请重试";
            r._$showFail2(i);
            t = t || e.ret;
            _gaq.push(["_trackEvent", "二维码登录", "【3】二维码登录结果", t ? t: "扫码登录失败"])
        };
        t.__confirmLogin = function(t) {
            _gaq.push(["_trackEvent", "二维码登录", "【2】二维码确认", "用户确认登录"])
        };
        t.__scanIsComplete = function(t) {
            _gaq.push(["_trackEvent", "二维码登录", "【1】扫码", "扫码成功"])
        };
        t.__codeLose = function(t) {
            _gaq.push(["_trackEvent", "二维码登录", "【-】二维码异常", "二维码失效"])
        }
    },
    1, 2, 3, 4, 6, 22, 61, 11, 27, 67, 29);
I$(7,
    function(l, e, n, o, i, r, d, c, a, f, u, h, s, _, p, m, v) {
        var t;
        _._$$LoginManager = l._$klass();
        t = _._$$LoginManager._$extend(h._$$Manager);
        t.__init = function(t) {
            i._$loadGaq();
            this.__options = t || {};
            window.$loginOpts = this.__options;
            this.__domains = t.domains || "";
            this.__product = t.product || "";
            this.__promark = t.promark || "";
            this.__host = t.host || "";
            this.__lazyCheck = t.lazyCheck || 0;
            this.__autoSuffix = t.autoSuffix || 0;
            this.__needUnLogin = t.needUnLogin || 0;
            this.__defaultUnLogin = t.defaultUnLogin || 0;
            this.__needQrLogin = t.needQrLogin || 0;
            this.__toolName = t.toolName || 0;
            this.__toolUrl = t.toolUrl || 0;
            this.__unLoginChecked = this.__defaultUnLogin || 0;
            this.__unLoginTime = t.unLoginTime || 10;
            this.__gaqo = {
                pid: this.__promark,
                pdt: this.__product
            };
            this.__domain = t.prdomain;
            window["$cookieDomain"] = t.cookieDomain;
            this.__errMsg = t.errMsg;
            this.__errMode = t.errMode || 1;
            this.__super(t);
            if (this.__errMsg) {
                var e = this;
                setTimeout(function() {
                        i._$showError(null, e.__errMsg, "nerror");
                        e.__errMsg = ""
                    },
                    20)
            }
            if (t.noGaq) _gaq.loadingFlag && clearTimeout(_gaq.loadingFlag);
            i._$resize()
        };
        t.__reset = function(t) {
            this.__super(t);
            this.__islogin = 1;
            this.__disabled = 1;
            this.__onpage = "login";
            this.__single = parseInt(t.single);
            this.__includeBox = !!t.includeBox;
            this.__mobileFirst = t.mobileFirst || 0;
            this.__regUrl = t.regUrl;
            this.__renderBox();
            i._$resize()
        };
        t.__renderLogin = function() {
            if (this.__needQrLogin) this.__showQrcodeBtn();
            this.__module = "goEmailLogin";
            this.__initLogin();
            this.__initUnLogin();
            this.__setUsername();
            this.__sendSize("init");
            this.__checkDisable();
            this.__inputs = e._$getByClassName(this.__box, "j-inputtext");
            this.__nameinput = this.__inputs[0];
            this.__passwordinput = this.__inputs[1];
            this.__checkcodeinput = this.__inputs[2];
            if (this.__domain && !this.__domain2) {
                this.__initUserNameDomain(this.__domain);
                this.__domain2 = 1
            }
            if (this.__placeholder && !this.__placeholder2) {
                if (this.__placeholder.account) {
                    var t = e._$getByClassName(this.__box, "u-input")[0];
                    e._$getByClassName(t, "u-label")[0].innerHTML = this.__placeholder.account;
                    var n = e._$getByClassName(t, "j-inputtext")[0];
                    e._$dataset(n, "placeholder", this.__placeholder.account)
                }
                if (this.__placeholder.pwd) {
                    var i = e._$getByClassName(this.__box, "u-input")[1];
                    e._$getByClassName(i, "u-label")[0].innerHTML = this.__placeholder.pwd;
                    var r = e._$getByClassName(i, "j-inputtext")[0];
                    e._$dataset(r, "placeholder", this.__placeholder.pwd)
                }
                this.__placeholder2 = 1
            }
        };
        t.__renderBox = function() {
            c._$render(this.__box, "index-tmp", {
                needMobileLogin: this.__options.needMobileLogin,
                goEmailLoginTxt: this.__options.goEmailLoginTxt || "网易邮箱帐号登录",
                goMbLoginTxt: this.__options.goMbLoginTxt || "网易手机帐号登录"
            });
            this.__cnt = e._$getByClassName(this.__box, "m-cnt")[0];
            this.__footer = e._$getByClassName(this.__box, "m-footer")[0];
            if (this.__includeBox) {
                var t = e._$get("cnt-box-parent");
                if (!e._$hasClassName(t, "cnt-box-include")) e._$addClassName(t, "cnt-box-include")
            }
            var n = a._$cookie("THE_LAST_LOGIN_MOBILE"),
                i = e._$get("mobileModule");
            if (this.__mobileFirst && i) this.__doAction(i);
            else this.__renderLogin()
        };
        t.__initUserNameDomain = function(i) {
            try {
                var t = e._$getByClassName(this.__box, "j-prdomain")[0];
                var n = e._$getByClassName(this.__box, "j-inputtext")[0];
                var r = e._$getByClassName(this.__box, "inputbox")[0];
                var s = e._$getByClassName(this.__box, "u-logo")[0];
                t.innerHTML = i;
                e._$delClassName(t, "f-dn");
                n.style.width = r.clientWidth - s.clientWidth - t.clientWidth - 22 + "px";
                t.style.right = -5 - t.clientWidth + "px"
            } catch(a) {}
        };
        t.__showQrcodeBtn = function() {
            this.__qrm = e._$getByClassName(this.__box, "j-btnqrcode")[0];
            if (this.__qrm) {
                e._$delClassName(this.__qrm, "f-dn");
                n._$clearEvent(this.__qrm);
                n._$addEvent(this.__qrm, "click", this.__showQrcodeModule._$bind(this))
            }
        };
        t.__hideQrcodeBtn = function() {
            if (this.__qrm) e._$addClassName(this.__qrm, "f-dn")
        };
        t.__doEnter = function(t) {
            if (this.__loginModule && "login" == this.__onpage) if (13 == t.keyCode && !this.__loginModule._stopEnter) this.__doAction(null, "dologin");
            else if (this.__loginModule) this.__loginModule._stopEnter = 0
        };
        t.__checkDisable = function() {
            var t = e._$get("dologin");
            if (this.__disabled) e._$addClassName(t, "btndisabled");
            else e._$delClassName(t, "btndisabled")
        };
        t.__destroy = function() {
            this.__clearModule();
            this.__super();
            this.__hideQrcodeBtn();
            delete this.__module
        };
        t.__initComp = function(t) {
            if (this.__loginModule) {
                this.__hasInit = 1;
                this.__capFlag = t ? t.capFlag: this.__capFlag;
                if (this.__capFlag) this.__showCheckCode(t)
            }
        };
        t.__onPwdKeyUp = function() {
            this.__pwdKeyUp = 1
        };
        t.__initLogin = function() {
            if (!this.__loginModule) this.__createLoginModule(1);
            if (this.__single) {
                var t = e._$get("changepage");
                if (this.__single) {
                    e._$dataset(t, "action", "none");
                    t.href = this.__regUrl ? this.__regUrl: "http://zc.reg.163.com/regInitialized";
                    t.target = "_blank"
                }
            }
        };
        t.__onDisabled = function(t) {
            this.__disabled = t;
            this.__checkDisable()
        };
        t.__createLoginModule = function(t) {
            var e = {
                pd: this.__product,
                pkid: this.__promark,
                pkht: this.__host
            };
            if (t) r._$request("initComponentLogin", e, this.__initComp._$bind(this), this.__showFail3._$bind(this), 1, this.__product);
            this.__loginModule = f._$$Login._$allocate({
                parent: this.__cnt,
                opts: this.__options,
                onSlideOk: this.__onSlideOk._$bind(this),
                onPwdKeyUp: this.__onPwdKeyUp._$bind(this),
                ondisabled: this.__onDisabled._$bind(this)
            });
            this.__onpage = "login";
            n._$delEvent(document, "keyup", this.__doEnter._$bind(this));
            n._$addEvent(document, "keyup", this.__doEnter._$bind(this))
        };
        t.__createMbLoginModule = function() {
            var t = this.__options.smsLoginFirst ? 0 : 1;
            this.__mbLoginModule = new _mm({
                data: this.__options
            });
            this.__mbLoginModule.$inject(this.__cnt);
            this.__mbLoginModule._$changeModule(t, this.__cnt)
        };
        t.__goModule = function() {
            this.__clearModule();
            if ("goEmailLogin" == this.__module) this.__renderLogin();
            else this.__createMbLoginModule()
        };
        t.__doAction = function(i, s) {
            var r = n._$getElement(i) || i,
                t = s || e._$dataset(r, "action");
            if ("goEmailLogin" == t || "goMbLogin" == t) {
                if (t == this.__module) return;
                this.__heads = e._$getByClassName(this.__box, "j-head");
                o._$forEach(this.__heads,
                    function(t) {
                        e._$delClassName(t, "active")
                    });
                e._$addClassName(r, "active");
                this.__module = t;
                this.__goModule()
            } else if ("dologin" == t) if (this.__isLeak) {
                if (this.__isLeak1) this.__doGoon(1);
                else if (this.__isLeak2) {
                    this.__isLeak = !1;
                    this.__doBack()
                } else if (this.__isLeak3) this.__doGoon()
            } else {
                this.__loginModule._$loginTime();
                this.__doLogin()
            } else if ("doback" == t) {
                this.__isLeak = !1;
                this.__doBack()
            } else if ("doclose" == t) if (!this.__closeFlag) {
                this.__closeFlag = !0;
                this.__sendClose()
            }
            this.__super(i)
        };
        t.__goonLog = function() {};
        t.__doGoon = function(i) {
            var e = {
                pd: this.__product,
                pkid: this.__promark,
                type: 0
            };
            if (1 == i) {
                var t = this.__loginModule._$getValues();
                t[0] = t[0].trim();
                e.un = this.__domain ? t[0] + this.__domain: t[0];
                r._$request("goonlog", e, this.__goonLog._$bind(this), this.__goonLog._$bind(this), 1, this.__product)
            }
            a._$cookie("THE_LAST_LOGIN", {
                value: this.__username || "",
                expires: 30
            });
            setTimeout(function() {
                this.__sendMsg({
                    type: "success",
                    username: this.__username || ""
                })
            }._$bind(this), 100)
        };
        t.__clearModule = function() {
            if (this.__mbLoginModule) this.__mbLoginModule.destroy();
            if (this.__loginModule) {
                this.__loginModule._$clearLoginTime();
                this.__loginModule = this.__loginModule._$recycle()
            }
            if (this.__qrcodeModule) this.__qrcodeModule = this.__qrcodeModule._$recycle()
        };
        t.__doBack = function() {
            this.__clearModule();
            if (this.__module && "goMbLogin" == this.__module) {
                this.__module = null;
                this.__doAction(e._$get("mobileModule"))
            } else {
                if (!this.__loginModule) this.__createLoginModule();
                this.__setUsername();
                this.__changePage();
                this.__loginModule._$loginTime();
                this.__initComp()
            }
            i._$resize()
        };
        t.__initUnLogin = function() {
            var t = function(i) {
                var n = i.target;
                var t = e._$getByClassName(this.__box, "u-checkbox")[0];
                if (!e._$hasClassName(t, "u-checkbox-select")) {
                    this.__unLoginChecked = 1;
                    e._$addClassName(t, "u-checkbox-select")
                } else {
                    this.__unLoginChecked = 0;
                    e._$delClassName(t, "u-checkbox-select")
                }
            };
            return function() {
                var i = e._$getByClassName(this.__box, "j-unlogn")[0];
                if (this.__needUnLogin && i) {
                    e._$delClassName(i, "f-dn");
                    var n = e._$getByClassName(i, "un-login")[0];
                    if (this.__unLoginChecked) e._$addClassName(e._$getByClassName(this.__box, "u-checkbox")[0], "u-checkbox-select");
                    this.__doInitDomEvent([[n, "click", t._$bind(this)]])
                } else e._$addClassName(i, "f-dn")
            }
        } ();
        t.__setUsername = function() {
            var t = this.__username || a._$cookie("THE_LAST_LOGIN");
            if (t) {
                var e;
                if (this.__domain) if (t.substring(t.indexOf("@")) === this.__domain) {
                    e = t.substring(0, t.indexOf("@"));
                    this.__loginModule._$hideLabel()
                } else e = "";
                else {
                    e = t;
                    this.__loginModule._$hideLabel()
                }
                this.__loginModule._$setUsername(e)
            }
        };
        t.__doLogin = function() {
            this.__loginModule._$stateOK(this.__doLoginCb._$bind(this))
        };
        t.__doLoginCb = function(e, t) {
            this.__pass = e;
            this.__errKey = t;
            if (e && this.__hasInit) this.__doLoginReal.call(this);
            else if (1 === this.__errMode) if ("email" === t && !this.__nameinput.value) i._$showError(this.__nameinput, "请输入帐号", "nerror");
            else if ("password" === t && !this.__passwordinput.value) i._$showError(this.__passwordinput, "请输入密码", "nerror");
            else if ("checkcode" === t && !this.__checkcodeinput.value) i._$showError(this.__checkcodeinput, "请输入验证码", "nerror");
            else if ("slidecap" === t) i._$showError(this.__checkcodeinput, "请滑动验证码", "nerror")
        };
        t.__doLoginReal = function() {
            return function() {
                if (this.__pass && this.__hasInit) {
                    i._$timeCount("LOGIN_TIME");
                    var e = this.__loginModule._$getValues(),
                        t = {};
                    e[0] = e[0].trim();
                    t.un = this.__domain ? e[0] + this.__domain: e[0];
                    t.pw = MP.encrypt2(e[1]);
                    t.pd = this.__product;
                    t.l = this.__unLoginChecked ? 1 : 0;
                    t.d = this.__unLoginTime;
                    t.t = (new Date).getTime();
                    t.pkid = this.__promark;
                    this.__username = t.un;
                    this.__password = e[1];
                    this.__safeLogin(t)
                }
            }
        } ();
        t.__safeLogin = function() {
            var t = function(i) {
                var t = [],
                    e = {};
                o._$forEach(i,
                    function(i) {
                        if (!e[i]) {
                            e[i] = 1;
                            t.push(i)
                        }
                    });
                return t
            };
            var e = function(n, e, r) {
                if (r) {
                    var s = n.split("@")[1];
                    e = e + (e ? ",": "") + s
                }
                e = e.replace("vip.188.com", "188.com");
                var i = e.split(",");
                i = t(i);
                return i.join(",")
            };
            return function(t) {
                t.domains = this.__domains || "";
                t.domains = e(t.un, t.domains, this.__autoSuffix);
                if (this.__lazyCheck && this.__capFlag) {
                    this.__dataTemp = t;
                    if (1 == this.__capFlag) this.__doLazyCheck();
                    else this.__doLazyCheckSlide()
                } else this.__getLoginTicket(t)
            }
        } ();
        t.__doLazyCheckSlide = function() {
            this.__loginModule._$verifyCap()
        };
        t.__onSlideOk = function() {
            if (this.__dataTemp) this.__getLoginTicket(this.__dataTemp)
        };
        t.__doLazyCheck = function(t) {
            var e = this.__loginModule._$getSmsValue();
            if (!this.__checkSmsCodeLock) {
                this.__checkSmsCodeLock = 1;
                var t = {
                    cap: e,
                    pd: this.__product,
                    pkid: this.__promark
                };
                r._$request("checkSmsCode", t, this.__cbSmsCode._$bind(this), this.__ckSmsCodeEx._$bind(this, "验证码输入错误"), 1, this.__product)
            }
        };
        t.__cbSmsCode = function(t) {
            this.__checkSmsCodeLock = 0;
            if (this.__dataTemp) this.__getLoginTicket(this.__dataTemp)
        };
        t.__ckSmsCodeEx = function(t, e) {
            this.__checkSmsCodeLock = 0;
            this.__loginModule.__ckSmsCodeEx(t, e)
        };
        t.__getLoginTicket = function(t) {
            if (!this.__getTkLock) {
                var e = {};
                e.un = t.un;
                this.__getTkLock = 1;
                r._$request("getLoginTicket", e, this.__gltSuccess._$bind(this, t), this.__gltWarn._$bind(this), 1, this.__product)
            }
        };
        t.__gltSuccess = function(t, e) {
            this.__getTkLock = 0;
            var n = e.ret;
            this.__tk = e.tk;
            if (201 == n) {
                t.tk = this.__tk;
                t.pwdKeyUp = this.__pwdKeyUp || 0;
                r._$request("safelogin", t, this.__loginSuccess._$bind(this), this.__cbWarn._$bind(this), 1, this.__product)
            } else {
                var a = s[n] || i._$getErrorTxt(e.ret);
                _gaq.push(["_trackEvent", "登录结果", "登录失败", "【" + n + "-gt】" + a]);
                i._$showError(null, a, "nerror")
            }
        };
        t.__gltWarn = function(t) {
            this.__getTkLock = 0;
            var e = t.ret || 0;
            this.__showCheckCode(t);
            var n = s[e] || i._$getErrorTxt(t.ret);
            var r = t.dt || "gt";
            _gaq.push(["_trackEvent", "登录结果", "登录失败", "【" + e + "-" + r + "】" + n]);
            if ("441" != e && "444" != e) i._$showError(null, n, "nerror")
        };
        t.__loginSuccess = function(t) {
            this.__pwdKeyUp = 0;
            _gaq.push(["_trackEvent", "登录结果", "登录成功", "【登录成功】from:" + this.__product + ",domain:" + this.__username.substring(this.__username.indexOf("@"))]);
            var e = i._$timeCountEnd("LOGIN_TIME");
            if (e > 0) {
                _gaq.push(["_trackEvent", "登录效率", "耗时【" + 50 * Math.ceil(e / 50) + "ms】", "详细耗时【" + e + "ms】"]);
                e = -1
            }
            a._$cookie("THE_LAST_LOGIN", {
                value: this.__username,
                expires: 30
            });
            this.__hideCheckCode();
            if (t.unprotectedGuide) {
                if (t.nextUrls) this.__setDomains({
                    type: "fksuccess",
                    username: this.__username || "",
                    nextUrls: t.nextUrls
                });
                this.__fkSetCookie(t);
                this.__showLeak(3)
            } else this.__setDomains({
                type: "success",
                username: this.__username || "",
                nextUrls: t.nextUrls
            })
        };
        t.__fkSetCookie = function(t) {
            var e = [],
                i = t.riskCKUrl || t.proGuideCKUrl;
            if (i) {
                e.push(i);
                this.__setDomains({
                    type: "fksuccess",
                    username: this.__username || "",
                    nextUrls: e
                })
            }
        };
        t.__cbWarn = function(e) {
            this.__pwdKeyUp = 0;
            var t = e.ret;
            if (e) {
                if ("423" == t) {
                    _gaq.push(["_trackEvent", "登录结果", "登录异常", "【423】风控帐号"]);
                    this.__setDomains({
                        type: "fksuccess",
                        username: this.__username || "",
                        nextUrls: e.nextUrls
                    });
                    this.__fkSetCookie(e);
                    this.__showLeak(1)
                } else if ("428" == t) {
                    _gaq.push(["_trackEvent", "登录结果", "登录异常", "【428】标记帐号"]);
                    this.__showLeak(2)
                } else if ("401" == t) {
                    var n = s[t] || i._$getErrorTxt(e.ret);
                    _gaq.push(["_trackEvent", "登录结果", "登录失败", "【401-" + (e.dt || "00") + "】参数错误"]);
                    i._$showError(null, n, "nerror")
                } else if ("501" == t) {
                    _gaq.push(["_trackEvent", "登录结果", "登录异常", "【501】"]);
                    this.__showFail(t)
                } else if ("500" == t) {
                    _gaq.push(["_trackEvent", "登录结果", "登录异常", "【500】"]);
                    this.__showFail(t)
                } else {
                    if (e.dt) t = t + "-" + e.dt;
                    if (t) {
                        var n = s[t] || i._$getErrorTxt(e.ret),
                            a = 424 == t || 425 == t || 426 == t || 422 == t || 602 == t ? 2 : 0;
                        if (425 == t) n = n.replace("#",
                            function() {
                                return i._$getCommonEmail(this.__username)
                            }._$bind(this));
                        _gaq.push(["_trackEvent", "登录结果", "登录失败", "【" + t + "】" + n]);
                        if (this.__capFlag) i._$showError(null, n, "nerror", a);
                        else if ("441" != t && "444" != t) i._$showError(null, n, "nerror", a)
                    }
                }
                var r = i._$timeCountEnd("LOGIN_TIME");
                if (r > 0) {
                    _gaq.push(["_trackEvent", "登录效率", "耗时【" + 50 * Math.ceil(r / 50) + "ms】", "详细耗时【" + r + "ms】"]);
                    r = -1
                }
            }
            this.__showCheckCode(e)
        };
        t.__showCheckCode = function(t) {
            var e;
            if (this.__loginModule) if (t) {
                if ("1" == t.capFlag) {
                    t.ret = "441";
                    e = 1
                }
                if ("4" == t.capFlag) {
                    t.ret = "444";
                    e = 1
                }
                if ("441" == t.ret) {
                    _gaq.push(["_trackEvent", "登录结果", "登录异常", "【411】需要图片验证码"]);
                    this.__capFlag = 1;
                    e = e || "请输入图片验证码";
                    this.__loginModule.__ckSmsCodeEx(e, t)
                } else if ("444" == t.ret) {
                    _gaq.push(["_trackEvent", "登录结果", "登录异常", "【444】需要滑块验证码"]);
                    this.__capFlag = 4;
                    e = e || "请滑动验证码";
                    this.__loginModule.__ckSmsCodeEx(e, t)
                } else if (this.__loginModule.__needSlideCap || this.__loginModule.__needCheckCode) {
                    var i = "44" + (this.__loginModule.__needSlideCap ? "4": "1");
                    e = 1;
                    t = {
                        ret: i
                    };
                    this.__loginModule.__ckSmsCodeEx(e, t)
                }
            }
        };
        t.__hideCheckCode = function() {
            this.__loginModule._$hideCheckCode()
        };
        t.__getCheckCode = function() {
            this.__loginModule._$getCheckCode()
        };
        t.__showQrcodeModule = function() {
            this.__clearModule();
            var t = e._$getByClassName(this.__box, "j-headimg")[0];
            if (!this.__qrcodeModule && "qrcode" != this.__onpage) {
                t.style.display = "none";
                var i = u._$$QrcodeManager._$allocate({
                    parent: this.__cnt,
                    opts: this.__options,
                    product: this.__product,
                    prdomain: this.__domain,
                    toolName: this.__toolName,
                    toolUrl: this.__toolUrl,
                    sendmsg: this.__setDomains._$bind(this)
                });
                this.__qrcodeModule = i;
                this.__onpage = "qrcode";
                e._$addClassName(this.__qrm, "pc")
            } else {
                t.style.display = "block";
                this.__doBack();
                this.__onpage = "login";
                e._$delClassName(this.__qrm, "pc")
            }
        };
        t.__showFail = function(t) {
            _gaq.push(["_trackEvent", "异常", "登录异常", "异常【" + (s[t] || "未知异常" + t) + "】"]);
            this.__super(t)
        };
        t.__showFail3 = function(t) {
            this.__hasInit = 0;
            var e = t && t.ret || "0";
            var i = this.__host || "未知产品";
            _gaq.push(["_trackEvent", "登录步骤", "【" + e + "】初始化", "初始化失败,from:" + i]);
            this.__showFail(e)
        };
        t.__showLeak = function(t) {
            this.__loginModule._$clearLoginTime();
            _gaq.push(["_trackEvent", "异常", "登录异常", "异常"]);
            this.__super(t);
            i._$resize()
        }
    },
    1, 2, 3, 4, 11, 21, 6, 22, 29, 30, 31, 27, 32);
I$(14,
    function(o, e, i, m, c, u, a, p, r, s, n, l, f, d) {
        var t, _, h = {
            1 : "red",
            2 : "orange",
            3 : "green",
            4 : "blue"
        };
        u._$parseTemplate("jst-template");
        n._$$Index = o._$klass();
        t = n._$$Index._$extend(c._$$EventTarget);
        t.__init = function(t) {
            this.__super(t);
            this.__loadConfig(t);
            i._$addEvent(document, "click", this.__changePage._$bind(this))
        };
        t.__reset = function(t) {
            this.__super(t)
        };
        t.__destroy = function() {
            this.__super()
        };
        t.__doClose = function() {
            var t = {
                type: "close"
            };
            t["URS-CM"] = 1;
            r._$postMessage("_parent", {
                data: t
            })
        };
        t.__loadConfig = function(t) {
            if (t) {
                t.single = t.single || 0;
                this.__page = t.page || "login";
                if (t.notFastReg) {
                    this.__page = "login";
                    t.single = 1
                }
                _ = t.needanimation;
                window.isHttps = t.isHttps || 0;
                window.PROTOCOL = "http" != t.PROTOCOL ? "http://": "https://";
                window.REGPROTOCOL = "http" == t.REGPROTOCOL ? "http://": "https://";
                if (window.isHttps) {
                    window.PROTOCOL = "https://";
                    window.REGPROTOCOL = "https://"
                }
                this.__loadStyle(t);
                this.__opt = t;
                this.__showPage()
            } else this.__doClose()
        };
        t.__loadStyle = function(n) {
            var t = n.skin || "0";
            var s = n.cssFiles || "";
            var r = n.style || "";
            if (r) e._$addStyle(r);
            else if (!s && 0 != t) {
                t = h[t] || "red";
                var i = document.createElement("link");
                i.rel = "stylesheet";
                i.type = "text/css";
                i.href = "../../webapp/res/css/" + t + ".css";
                document.getElementsByTagName("head")[0].appendChild(i)
            }
        };
        t.__changePage = function(r) {
            var n = i._$getElement(r),
                a = e._$dataset(n, "action"),
                o = e._$dataset(n, "mdtype"),
                t;
            if ("changepage" == a) {
                this.__mdType = o;
                this.__page = "login" == this.__page ? "register": "login";
                if ("login" == this.__page && e._$get("VIP")) e._$get("VIP").style.display = "none";
                t = {
                    type: "changepage",
                    page: this.__page,
                    mdtype: this.__mdType || ""
                };
                t["URS-CM"] = 1;
                s._$postMessage("_parent", {
                    data: t
                });
                this.__showPage(1)
            }
        };
        t.__showPage = function(i) {
            s._$hideFail();
            this.__opt.page = this.__page;
            if (i) {
                this.__opt.mobileFirst = this.__mdType ? 1 : 0;
                var t = e._$get("cnt-box-parent");
                e._$addClassName(t, "switching");
                setTimeout(function() {
                        e._$delClassName(t, "switching")
                    },
                    0)
            }
            setTimeout(function() {
                if (this.__lg) this.__lg = this.__lg._$recycle();
                this.__lg = a._$$LoginManager._$allocate(this.__opt)
            }._$bind(this), 0)
        };
        i._$addEvent(document, "templateready",
            function() {
                var t = function(e) {
                    var t = e.data;
                    if (t) {
                        if ("string" == typeof t) try {
                            t = JSON.parse(t)
                        } catch(i) {}
                        if ("object" == typeof t && "URS|" == t.from) {
                            window.URSCONFIG = t;
                            n._$$Index._$allocate(t)
                        }
                    }
                };
                i._$addEvent(window, "message", t);
                r._$postMessage("_parent", {
                    data: {
                        "URS-READY": 1
                    }
                })
            })
    },
    1, 2, 3, 4, 5, 6, 7, 9, 10, 11);