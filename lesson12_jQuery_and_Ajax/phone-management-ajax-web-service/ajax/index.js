function addNewSmartPhone() {
    //lấy dữ liệu từ form html
    let producer = $('#producer').val();
    let model = $('#model').val();
    let price = $('#price').val();
    let newSmartphone = {
        producer: producer,
        model: model,
        price: price
    };
    // gọi phương thức ajax
    $.ajax({
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        type: "POST",
        data: JSON.stringify(newSmartphone),
        //tên API
        url: "http://localhost:8080/api/smartphones",
        //xử lý khi thành công
        success: successHandler

    });
    //chặn sự kiện mặc định của thẻ
    event.preventDefault();
}

function successHandler() {
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/api/smartphones",
        success: function (data) {
            let content = '<table id="display-list" border="1"><tr>' +
                '<th>Producer</th>' +
                '<th>Model</th>' +
                '<th>Price</th>' +
                '<th>Delete</th>' +
                '</tr>';
            for (let i = 0; i < data.length; i++) {
                content += getSmartphone(data[i]);
            }
            content += "</table>";
            document.getElementById('smartphoneList').innerHTML = content;
            document.getElementById('smartphoneList').style.display = "block";
            document.getElementById('add-smartphone').style.display = "none";
            document.getElementById('title').style.display = "block";

            document.getElementById('display').style.display = "none";
            document.getElementById('display-create').style.display = "inline-block";
        }
    });
}

function displayFormCreate() {
    document.getElementById('smartphoneList').style.display = "none";
    document.getElementById('add-smartphone').style.display = "block";
    document.getElementById('display-create').style.display = "none";
    document.getElementById('title').style.display = "none";
}

function getSmartphone(smartphone) {
    return `<tr><td >${smartphone.producer}</td><td >${smartphone.model}</td><td >${smartphone.price}</td>` +
        `<td class="btn"><button class="deleteSmartphone" onclick="deleteSmartphone(${smartphone.id})">Delete</button></td></tr>`;
}

function editSmartphone(id, producer, model, price) {
    // Gắn dữ liệu cũ vào form
    $('#producer').val(producer);
    $('#model').val(model);
    $('#price').val(price);

    // Hiện form, đổi nút Add thành Update
    document.getElementById('add-smartphone').style.display = "block";
    document.getElementById('smartphoneList').style.display = "none";
    document.getElementById('display-create').style.display = "none";

    // Gắn sự kiện cập nhật
    $('#add-smartphone').off('submit').on('submit', function (event) {
        event.preventDefault();
        let newSmartphone = {
            producer: $('#producer').val(),
            model: $('#model').val(),
            price: $('#price').val()
        };
        $.ajax({
            headers: {'Accept': 'application/json', 'Content-Type': 'application/json'},
            type: "PUT",
            url: `http://localhost:8080/api/smartphones/${id}`,
            data: JSON.stringify(newSmartphone),
            success: function () {
                successHandler();
            }
        });
    });
}

// function deleteSmartphone(id) {
//     $.ajax({
//         type: "DELETE",
//         url: `http://localhost:8080/api/smartphones/${id}`,
//         success: successHandler
//     });
// }

function deleteSmartphone(id) {
    $(`#row-${id}`).fadeOut(300, function() {
        $.ajax({
            type: "DELETE",
            url: `http://localhost:8080/api/smartphones/${id}`,
            success: successHandler
        });
    });
}

function searchSmartphones() {
    let q = $('#searchQuery').val();
    let sortBy = $('#sortBy').val();
    let direction = $('#direction').val();

    $.ajax({
        type: "GET",
        url: `http://localhost:8080/api/smartphones/search?q=${q}&sortBy=${sortBy}&direction=${direction}`,
        success: function (data) {
            let content = '<table border="1"><tr><th>Producer</th><th>Model</th><th>Price</th><th>Action</th></tr>';
            for (let i = 0; i < data.length; i++) {
                content += getSmartphone(data[i]);
            }
            content += "</table>";
            $('#smartphoneList').html(content);
            $('#smartphoneList').fadeIn(500);
        }
    });
}
