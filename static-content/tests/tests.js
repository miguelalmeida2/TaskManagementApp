// Example test suite
describe('Math', function () {
    describe('#add()', function () {
        it('should return the sum of two numbers', function () {
            chai.expect(2 + 2).to.equal(4);
        });
    });
});