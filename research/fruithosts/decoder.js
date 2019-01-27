window.d = function(_0x5ecd00, _0x184b8d) {
	// 4
	k = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	// 6
	var _0x59b81a = '';
	// 5
	var _0x2e4782;
	var _0x2c0540;
	var _0x5a46ef;
	// 0
	var _0x4a2f3a;
	var _0x29d5bf;
	var _0x3b6833;
	var _0x426d70;
	// 7
	var _0x1598e0 = 0x0;
	// 3
	k = k.split('').reverse().join('');
	// 2
	_0x5ecd00 = _0x5ecd00.replace(/[^A-Za-z0-9\+\/\=]/g, '');
	// 1
	while (_0x1598e0 < _0x5ecd00.length) {
		// 6
		_0x4a2f3a = k.indexOf(_0x5ecd00.charAt(_0x1598e0++));
		// 2
		_0x29d5bf = k.indexOf(_0x5ecd00.charAt(_0x1598e0++));
		// 9
		_0x3b6833 = k.indexOf(_0x5ecd00.charAt(_0x1598e0++));
		// 8
		_0x426d70 = k.indexOf(_0x5ecd00.charAt(_0x1598e0++));
		// 5
		_0x2e4782 = (_0x4a2f3a << 0x2) | (_0x29d5bf >> 0x4);
		// 4
		_0x2c0540 = ((_0x29d5bf & 0xf) << 0x4) | ( _0x3b6833 >> 0x2);
		// 7
		_0x5a46ef = ((_0x3b6833 & 0x3) << 0x6) | _0x426d70;
		// 10
		_0x2e4782 = _0x2e4782 ^ _0x184b8d;
		// 0
		_0x59b81a = _0x59b81a + String.fromCharCode(_0x2e4782);
		// 3
		if (_0x3b6833 != 0x40) {
			_0x59b81a = _0x59b81a + String.fromCharCode(_0x2c0540);
		}
		// 1
		if (_0x426d70 != 0x40) {
			_0x59b81a = _0x59b81a + String.fromCharCode(_0x5a46ef);
		}
	}
	// 8
	return _0x59b81a;
}
